package com.fet.venus.db.couchbase.dao;

import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CouchbaseCacheDao {

    private final CouchbaseCacheConfig cacheConfig;

    public <T> Optional<T> findByRepository(
            String userCacheKey,
            Supplier<Optional<T>> cacheFunction,
            Supplier<Optional<T>> dbFunction,
            Consumer<T> cacheSaveFunction
    ) {
        if (cacheConfig.isQueryCache(userCacheKey)) {
            return cacheFunction.get().or(() -> {
                Optional<T> dbResult = dbFunction.get();
                dbResult.ifPresent(cacheSaveFunction);
                return dbResult;
            });
        } else {
            return dbFunction.get();
        }
    }

    public <T> T saveByRepository(
            String userCacheKey,
            T entity,
            Function<T, T> cacheFunction,
            Function<T, T> dbFunction
    ){
        if (cacheConfig.isQueryCache(userCacheKey)) {
            saveAsync(entity, dbFunction);
            return cacheFunction.apply(entity);
        } else {
            return dbFunction.apply(entity);
        }
    }

    @Async
    protected  <T> T saveAsync(T entity, Function<T, T> saveFunction) {
        return saveFunction.apply(entity);
    }


}
