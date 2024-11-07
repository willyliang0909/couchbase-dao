package com.fet.venus.db.couchbase.config;

import com.couchbase.client.java.codec.Transcoder;
import org.springframework.data.couchbase.cache.DefaultCouchbaseCacheWriter;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.time.Duration;

public class CouchbaseBeanCacheWriter extends DefaultCouchbaseCacheWriter {

    private final Class<?> clazz;
    private final CouchbaseTemplate template;

    public CouchbaseBeanCacheWriter(CouchbaseTemplate template, Class<?> clazz) {
        super(template.getCouchbaseClientFactory());
        this.template = template;
        this.clazz = clazz;
    }


    @Override
    public void put(String collectionName, String key, Object value, Duration expiry, Transcoder transcoder) {
        template.save(value);
    }

    @Override
    public Object get(String collectionName, String key, Transcoder transcoder, Class<?> clazz) {
        return template.findById(this.clazz).one(key);
    }



}
