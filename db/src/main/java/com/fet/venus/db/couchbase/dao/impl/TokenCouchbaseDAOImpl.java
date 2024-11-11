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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Primary
@RequiredArgsConstructor
@Repository("tokenCBDAO")
public class TokenCouchbaseDAOImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    private final TokenCouchbaseRepository couchbaseRepository;

    private final TokenDAOJPAImpl tokenDAOJPA;

    private final CouchbaseCacheConfig cacheConfig;

    private final CouchbaseCacheDao cacheDao;

    @Override
//    @TokenUpdate
    public void insertToken(Token token) {
        cacheDao.saveByRepository(
                CacheKey.USE_TOKEN_CACHE,
                token,
                couchbaseRepository::save,
                t -> {
                    tokenDAOJPA.insertToken(t);
                    return t;
                }
        );
    }

    @Override
//    @TokenUpdate
    public void updateToken(Token token) {
        cacheDao.saveByRepository(
                CacheKey.USE_TOKEN_CACHE,
                token,
                couchbaseRepository::save,
                t -> {
                    tokenDAOJPA.insertToken(t);
                    return t;
                }
        );
    }

    @Override
    public Token selectTokenByToken(String token) {
        return cacheDao.findByRepository(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository.findById(token),
                () -> Optional.ofNullable(tokenDAOJPA.selectTokenByToken(token)),
                couchbaseRepository::save
        ).orElse(null);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return cacheDao.findByRepository(
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
