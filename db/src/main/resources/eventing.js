function OnUpdate(doc, meta, xattrs) {
    log("Doc created/updated", meta.id);
    refreshCache(doc);
}

function OnDelete(meta, options) {
    log("Doc deleted/expired", meta.id);
}

function refreshCache(doc) {
    const _class = 'com.fet.venus.db.models.LatestCacheRevamp';
    const buffer_type = doc.bufferType;

    if ( doc._class != _class ) {
        return;
    }

    const query =
        'SELECT revamp.* ' +
        'FROM `vod`.`friday`.`venus_latest_cache_revamp` revamp ' +
        'WHERE _class = $1 AND bufferType = $2 ' +
        'ORDER BY revamp.`orderId` ASC';

    const results = N1QL(query, [_class, buffer_type], { isPrepared: true });

    const cache_array = []
    for (var item of results) {   // Stream results using 'for' iterator.
        cache_array.push(item)
    }
    results.close();

    const cache_key = `latest_revamp::${buffer_type}`
    cache_bucket[cache_key] = cache_array
}