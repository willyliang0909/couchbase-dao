package com.fet.venus.db.couchbase.config;

import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.codec.JsonTranscoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CouchbaseCacheConfig {

    private ConcurrentHashMap<String, Boolean> cacheMap;
    private final String cacheableKey = "cacheable";

    @PostConstruct
    public void init() {
        cacheMap = new ConcurrentHashMap<>();
        cacheMap.put(cacheableKey, Boolean.TRUE);
    }

    @Cacheable(value = CacheKey.CONFIG_CACHE, key = "#cacheFlag", condition = "#root.target.isCacheable()")
    public boolean isQueryCache(String cacheFlag) {
        return true;
    }

    public boolean isCacheable() {
        return Objects.equals(Boolean.TRUE, cacheMap.get(cacheableKey));
    }

    public boolean setCacheable(Boolean value) {
        cacheMap.compute(cacheableKey, (k, v) -> value);
        return isCacheable();
    }

    @Bean
    public CouchbaseCacheManager cacheManager(CouchbaseTemplate template) {

        JsonTranscoder jsonTranscoder = JsonTranscoder.create(JacksonJsonSerializer.create());

        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager
                .builder(template.getCouchbaseClientFactory().withScope("cache"))
                .cacheDefaults(CouchbaseCacheConfiguration
                        .defaultCacheConfig()
                        .disableCachingNullValues()
                        .valueTranscoder(jsonTranscoder)
                        .collection("data")
                );

        builder.withCacheConfiguration(CacheKey.BINARY_CACHE,
                CouchbaseCacheConfiguration
                        .defaultCacheConfig()
                        .disableCachingNullValues()
                        .collection("data")
        );

        return builder.build();
    }

}
