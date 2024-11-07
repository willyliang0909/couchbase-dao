package com.fet.venus.db.couchbase.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.data.couchbase.cache.CouchbaseCacheWriter;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

public class CouchbaseCacheManagerFactoryBean implements FactoryBean<CouchbaseCacheManager> {

    private final CouchbaseTemplate template;
    private final String cacheName;
    private final Class<?> cacheType;


    public CouchbaseCacheManagerFactoryBean(CouchbaseTemplate template, String cacheName, Class<?> cacheType) {
        this.template = template;
        this.cacheName = cacheName;
        this.cacheType = cacheType;
    }

    @Override
    public CouchbaseCacheManager getObject() {
        CouchbaseCacheWriter cacheWriter = new CouchbaseBeanCacheWriter(template, cacheType);

        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager
                .builder(cacheWriter);

        builder.withCacheConfiguration(cacheName, CouchbaseCacheConfiguration
                .defaultCacheConfig()
                .computePrefixWith(cacheName -> "")
                .collection(cacheName)
        );

        return builder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return CouchbaseCacheManager.class;
    }
}
