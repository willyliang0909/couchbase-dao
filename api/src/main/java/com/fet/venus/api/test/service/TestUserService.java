package com.fet.venus.api.test.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fet.venus.api.test.model.TestUser;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestUserService {

    private final CouchbaseCacheDao cacheDao;

    public TestUser getUser() {
        String cacheName = CacheKey.USER_CACHE;
        String key = "test_user";
        return cacheDao.get(
                CacheKey.USE_USER_CACHE,
                () -> cacheDao.get(cacheName, key, TestUser.class),
                () -> newUser("willy", 10L),
                entity -> cacheDao.save(cacheName, key, entity)
        ).orElse(null);
    }

    protected TestUser newUser(String name, Long age) {
        return TestUser.builder()
                .name(name)
                .age(age)
                .address(TestUser.Address.builder()
                        .country("tw")
                        .city("kh")
                        .street("street 123")
                        .build()
                ).build();
    }

    public List<TestUser> list() {
        String cacheName = CacheKey.USER_CACHE;
        String key = "list_user";
        return cacheDao.get(
                CacheKey.USE_USER_CACHE,
                () -> cacheDao.get(cacheName, key, new TypeReference<>() {}),
                () -> List.of(
                        newUser("a", 10L),
                        newUser("b", 20L),
                        newUser("c", 30L)
                ),
                entity -> cacheDao.save(cacheName, key, entity)
        ).orElse(new ArrayList<>());
    }

}
