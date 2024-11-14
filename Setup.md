
### couchbase install
```shell
docker run -d --name couchbase -p 8091-8096:8091-8096 -p 11210-11211:11210-11211 couchbase:enterprise-7.6.3
```

create bucket
```shell
curl -X POST http://localhost:8091/pools/default/buckets \
-u Administrator:couchbase \
-d name=vod \
-d ramQuota=100 
```

**cache**
create scope
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes \
-u Administrator:couchbase \
-d name=cache
```
create collection
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes/cache/collections \
-u Administrator:couchbase \
-d name=data
```

**vod**
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes \
-u Administrator:couchbase \
-d name=friday
```
create collection
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes/friday/collections \
-u Administrator:couchbase \
-d name=venus_token
```
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes/friday/collections \
-u Administrator:couchbase \
-d name=venus_latest_cache_revamp
```

**eventing**
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes \
-u Administrator:couchbase \
-d name=eventing
```
create collection
```shell
curl -X POST http://localhost:8091/pools/default/buckets/vod/scopes/eventing/collections \
-u Administrator:couchbase \
-d name=venus_latest_cache_revamp
```

import eventing
```shell
curl -X POST -d @./db/src/main/resources/latest_cache_event.json \
http://localhost:8096/api/v1/import \
-u Administrator:couchbase 
```
