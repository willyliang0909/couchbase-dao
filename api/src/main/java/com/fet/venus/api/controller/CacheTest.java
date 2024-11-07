package com.fet.venus.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fet.venus.api.test.service.TestUserService;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.api.test.model.TestUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@Slf4j
@RequiredArgsConstructor
public class CacheTest {

    private final CouchbaseCacheConfig cacheConfig;

    private final ObjectMapper objectMapper;

    private final TestUserService testUserService;

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
        var result = testUserService.getUser();
        return objectMapper.convertValue(result, TestUser.class);
    }

}
