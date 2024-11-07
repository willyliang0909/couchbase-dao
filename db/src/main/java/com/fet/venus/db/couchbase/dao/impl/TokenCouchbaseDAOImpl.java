package com.fet.venus.db.couchbase.dao.impl;

import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.db.couchbase.dao.aop.TokenUpdate;
import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.dao.impl.AbstractHibernateDaoImpl;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository("tokenCBDAO")
public class TokenCouchbaseDAOImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    private final TokenCouchbaseRepository couchbaseRepository;

    private final TokenDAOJPAImpl tokenDAOJPA;

    private final CouchbaseCacheConfig cacheConfig;

    @Override
    @TokenUpdate
    public void insertToken(Token token) {
        if (isQueryCache()) {
            couchbaseRepository.save(token);
        } else {
            tokenDAOJPA.insertToken(token);
        }
    }

    @Override
    @TokenUpdate
    public void updateToken(Token token) {
        if (isQueryCache()) {
            couchbaseRepository.save(token);
        } else {
            tokenDAOJPA.updateToken(token);
        }
    }

    @Override
    public Token selectTokenByToken(String token) {
        if (isQueryCache()) {
            return couchbaseRepository.findById(token).orElseGet(() -> {
                var result = tokenDAOJPA.selectTokenByToken(token);
                if (result != null) {
                    couchbaseRepository.save(result);
                }
                return result;
            });
        } else {
            return tokenDAOJPA.selectTokenByToken(token);
        }
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        if (isQueryCache()) {
            return couchbaseRepository
                    .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                    .findFirst()
                    .orElseGet(() -> {
                        var result = tokenDAOJPA.selectTokenByFetToken(fetToken);
                        if (result != null) {
                            couchbaseRepository.save(result);
                        }
                        return result;
                    });
        } else {
            return tokenDAOJPA.selectTokenByFetToken(fetToken);
        }

    }

    private boolean isQueryCache() {
        return cacheConfig.isQueryCache(CacheKey.USE_TOKEN_CACHE);
    }
}
