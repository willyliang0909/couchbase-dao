package com.fet.venus.api.test.service;

import com.fet.venus.api.test.model.TestUser;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestUserService {

    private final CouchbaseCacheConfig cacheConfig;

    private final CacheManager cacheManager;

    @Cacheable(value = CacheKey.USER_CACHE, key = "'test'", condition = "#root.target.isQueryCache()")
    public TestUser getUser() {
        log.info("get new TestUser");
        return TestUser.builder()
                .name("willy")
                .age(10L)
                .address(TestUser.Address.builder()
                        .country("tw")
                        .city("kh")
                        .street("street 123")
                        .build()
                ).build();
    }

    public boolean isQueryCache() {
        return cacheConfig.isQueryCache(CacheKey.USE_USER_CACHE);
    }
}
