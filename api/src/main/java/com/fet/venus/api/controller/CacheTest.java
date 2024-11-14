package com.fet.venus.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fet.venus.api.test.service.TestUserService;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.api.test.model.TestUser;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cache")
@Slf4j
@RequiredArgsConstructor
public class CacheTest {

    private final CouchbaseCacheConfig cacheConfig;

    private final CouchbaseCacheDao cacheDao;

    private final TestUserService testUserService;

    @GetMapping("/binary/{key}/{value}")
    @Cacheable(value = CacheKey.BINARY_CACHE, key = "#key")
    public String cacheBinary(@PathVariable String key, @PathVariable String value) {
        log.info("return key: {}, value: {}", key, value);
        return value;
    }

    @GetMapping("/binary/testuser")
    @Cacheable(value = CacheKey.BINARY_CACHE, key = "'binaryTestUser'")
    public TestUser binaryTestUser() {
        log.info("new TestUser");
        return testUserService.newUser("user", 40L);
    }


    //500 - Internal Server Error"
    @GetMapping("/listUser/cachable")
    public List<TestUser> getCacheUser() {
        var list = testUserService.cacheUser();
        var name = list.stream().findFirst().map(TestUser::getName).orElse(null);
        return list;
    }

    @GetMapping("/listUser")
    public List<TestUser> getUser() {
        var list = cacheDao
                .get(CacheKey.USER_CACHE, "list_user", new TypeReference<List<TestUser>>() {})
                .orElse(new ArrayList<>());
        var name = list.stream().findFirst().map(TestUser::getName).orElse(null);
        return list;
    }

    @PostMapping("/listUser")
    public void saveUser() {
        cacheDao.save(CacheKey.USER_CACHE, "list_user", List.of(
                testUserService.newUser("a", 10L),
                testUserService.newUser("b", 20L),
                testUserService.newUser("c", 30L)
        ));
    }

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

}
