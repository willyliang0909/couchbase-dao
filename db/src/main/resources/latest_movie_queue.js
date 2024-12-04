function OnUpdate(doc, meta, xattrs) {

    if (meta.id == 'next') {
      log('next');
      return;
    }

    const data = doc.data;
    if (Array.isArray(data) && data.length > 0) {
        //continue
    } else {
        log("No data found");
        return;
    }

    publishByBufferType(doc, meta.id);

}

function OnDelete(meta, options) {
    log("Doc deleted/expired", meta.id);
}


function publishByBufferType(doc, id) {
    const bufferType = doc.data[0].bufferType;
    const publishDateTime = doc.publishDateTime || 0

    if (publishDateTime < Date.now()) {
        publishCache(id, bufferType)
    } else {
        updateNextQueue(bufferType);
    }
}

function executeQuery(query, params) {
    const results = N1QL(query, params, { isPrepared: true });

    const result_array = []
    for (var item of results) {
        result_array.push(item);
    }
    results.close();

    return result_array;
}

function publishCache(id, bufferType) {
    log('publish')

    const query =
        'SELECT data.* ' +
        'FROM `vod`.`friday`.`venus_latest_cache_revamp_queue` queue ' +
        'UNNEST queue.data AS data ' +
        'WHERE META(queue).id  = $1 ' +
        'ORDER BY data.orderId';

    const cache_array = executeQuery(query, [id]);

    //write to cache
    revamp['cache_latest_revamp::bufferType:' + bufferType] = cache_array;

    //move from queue to revamp
    let doc = self[id]
    doc.actualPublishDateTime = new Date().toISOString()
    revamp[id] = doc;
    delete self[id]

    updateNextQueue(bufferType);

    writeBackToDB(cache_array)
}


function updateNextQueue(bufferType) {
    log('updateNextQueue, bufferType: ', bufferType)

    const query =
    'SELECT META(q).id, MILLIS_TO_STR(q.publishDateTime) as publishDateTime ' +
    'FROM `vod`.`friday`.`venus_latest_cache_revamp_queue` q ' +
    'WHERE q.publishDateTime > NOW_MILLIS() ' +
    'AND META(q).id != "next" ' +
    'AND q.data[0].bufferType = $1 '
    'ORDER BY q.publishDateTime ' +
    'LIMIT 1';

    const cache_array = executeQuery(query, [bufferType]);
    let next = cache_array[0];

    //update timer
    let timer_key = 'bufferType:' + bufferType;
    if (next) {
        const context = {
            "id": next.id,
            "bufferType": bufferType
        }
        log('context', context)
        let publishDateTime = new Date(next.publishDateTime)
        publishDateTime.setSeconds(publishDateTime.getSeconds() - 5);
        createTimer(TimerCallback, publishDateTime, timer_key, context);
    } else {
        cancelTimer(CancelCallback, timer_key)
        next = {}
    }

    //update 'next' document
    let status = self['next'] ?? {};
    let buffer_type = status['buffer_type'] ?? {};
    buffer_type[bufferType] = next;
    status.buffer_type = buffer_type;

    self['next'] = status;
}

function TimerCallback(context) {
    var now = new Date();
    log('TimerCallback', now, context.id);
    publishCache(context.id, context.bufferType);
}

function CancelCallback() {
    log("cancelTimer")
}


function writeBackToDB(doc) {
    log("writeBack")

    var request = {
        path: '/latest-revamp/sync',
        body: doc
    };

    var response = curl('POST', api_endpoint, request);
    if (response.status == 200) {
      log("Successfully sync");
    }
}