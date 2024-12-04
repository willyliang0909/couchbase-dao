package com.fet.venus.db.couchbase.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouchbaseCacheDao {

    private final CouchbaseCacheConfig cacheConfig;

    private final CouchbaseCacheManager cacheManager;

    private final ObjectMapper objectMapper;

    public <T> Optional<T> get(String cacheName, String key, Class<T> clazz) {
        Cache cache = cacheManager.getCache(cacheName);
        assert cache != null;
        return Optional.ofNullable(cache.get(key, clazz));
    }

    public <T> Optional<T> get(String cacheName, String key, TypeReference<T> type) {
        return get(cacheName, key, Object.class)
                .map(o -> objectMapper.convertValue(o, type));
    }

    public void save(String cacheName, String key, Object entity) {
        Cache cache = cacheManager.getCache(cacheName);
        assert cache != null;
        cache.put(key, entity);
    }

    public <T> Optional<T> get(
            String userCacheKey,
            Supplier<Optional<T>> cacheFunction,
            Supplier<T> getFunction,
            Consumer<T> cacheSaveFunction
    ) {
        if (cacheConfig.isQueryCache(userCacheKey)) {
            log.info("search from cache");
            Optional<T> cacheResult = cacheFunction.get();
            if (cacheResult.isPresent()) {
                log.info("found in cache");
                return cacheResult;
            } else {
                log.info("data is not in cache, search from db");
                T getResult = getFunction.get();
                if (getResult != null) {
                    log.info("found in db");
                    cacheSaveFunction.accept(getResult);
                    log.info("saved to cache");
                }
                return Optional.ofNullable(getResult);
            }
        } else {
            log.info("cache is disabled");
            return Optional.ofNullable(getFunction.get());
        }
    }

    public <T> T save(
            String userCacheKey,
            T entity,
            Function<T, T> cacheFunction,
            Function<T, T> saveFunction
    ){
        if (cacheConfig.isQueryCache(userCacheKey)) {
            log.info("save to cache");
            saveAsync(entity, saveFunction);
            return cacheFunction.apply(entity);
        } else {
            return saveFunction.apply(entity);
        }
    }

    protected <T> void saveAsync(T entity, Function<T, T> saveFunction) {
        log.info("save to db");
        CompletableFuture
                .runAsync(() -> {
                    log.info("saving to db");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    saveFunction.apply(entity);
                    log.info("saved to db");
                });
    }

}
