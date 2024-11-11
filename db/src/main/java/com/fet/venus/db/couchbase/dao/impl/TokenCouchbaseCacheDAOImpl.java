package com.fet.venus.db.couchbase.dao.impl;

import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.dao.impl.AbstractHibernateDaoImpl;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("tokenCBCacheDAO")
@RequiredArgsConstructor
@CacheConfig(
        cacheNames = "token",
        cacheManager = "cacheTokenManager"
)
public class TokenCouchbaseCacheDAOImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    private final TokenCouchbaseRepository couchbaseRepository;

    private final TokenDAOJPAImpl tokenDAOJPA;

    private final CouchbaseCacheConfig cacheConfig;

    private final CouchbaseCacheDao couchbaseCacheDao;

    @Override
    @CachePut(key = "#toke.token", condition = "#root.target.isQueryCache()")
    public void insertToken(Token token) {
        tokenDAOJPA.insertToken(token);
    }

    @Override
    @CachePut(key = "#toke.token", condition = "#root.target.isQueryCache()")
    public void updateToken(Token token) {
        tokenDAOJPA.updateToken(token);
    }

    @Override
    @Cacheable(key = "#token", condition = "#root.target.isQueryCache()")
    public Token selectTokenByToken(String token) {
        return tokenDAOJPA.selectTokenByToken(token);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return couchbaseCacheDao.findByRepository(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository
                        .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                        .findFirst(),
                () -> Optional.ofNullable(tokenDAOJPA.selectTokenByToken(fetToken)),
                couchbaseRepository::save
        ).orElse(null);
    }

    private boolean isQueryCache() {
        return cacheConfig.isQueryCache(CacheKey.USE_TOKEN_CACHE);
    }
}
