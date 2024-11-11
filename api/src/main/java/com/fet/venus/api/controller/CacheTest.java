package com.fet.venus.api.controller;

import com.fet.venus.api.test.service.TestUserService;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.api.test.model.TestUser;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cache")
@Slf4j
@RequiredArgsConstructor
public class CacheTest {

    private final CouchbaseCacheConfig cacheConfig;

    private final TestUserService testUserService;

    private final CouchbaseCacheDao dao;

    @GetMapping("/isCacheable")
    public boolean test() {
        return cacheConfig.isCacheable();
    }

    @GetMapping("/isCacheable/{value}")
    public boolean setByPassCache(@PathVariable boolean value) {
        return cacheConfig.setCacheable(value);
    }

    @GetMapping("/isQueryCache/{key}")
    public boolean isQueryCache(@PathVariable String key) {
        return cacheConfig.isQueryCache(key);
    }

    @GetMapping("/testUser")
    public TestUser user() {
        return testUserService.getUser();
    }

    @GetMapping("/listUser")
    public List<TestUser> listUser() {
        var result = testUserService.list();
        var user = result.stream().findFirst().get();
        return result;
    }

    @GetMapping("/dao/testUser")
    public TestUser daoUser() {
        return dao.get(CacheKey.USER_CACHE, "test", TestUser.class).orElse(null);
    }
}
