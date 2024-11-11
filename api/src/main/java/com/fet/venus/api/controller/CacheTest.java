package com.fet.venus.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fet.venus.api.test.service.TestUserService;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.api.test.model.TestUser;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/cache")
@Slf4j
@RequiredArgsConstructor
public class CacheTest {

    private final CouchbaseCacheConfig cacheConfig;

    private final ObjectMapper objectMapper;

    private final TestUserService testUserService;

    private final CouchbaseCacheDao dao;

    private final TokenCouchbaseRepository repository;

    private final TokenDAOJPAImpl daojpa;

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

    @GetMapping("/dao/{token}")
    public Token dao(@PathVariable String token) {
        return dao.findByRepository(CacheKey.CONFIG_CACHE,
                () -> repository.findById(token),
                () -> Optional.ofNullable(daojpa.selectTokenByToken(token)),
                repository::save
        ).orElse(null);
    }

}
