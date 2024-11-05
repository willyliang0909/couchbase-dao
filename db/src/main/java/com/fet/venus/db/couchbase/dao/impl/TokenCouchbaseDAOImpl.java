package com.fet.venus.db.couchbase.dao.impl;

import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.dao.impl.AbstractHibernateDaoImpl;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository("tokenCBDAO")
public class TokenCouchbaseDAOImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    @Autowired
    private TokenCouchbaseRepository couchbaseRepository;

    @Autowired
    private TokenDAOJPAImpl tokenDAOJPA;

    @Override
    public void insertToken(Token token) {
        couchbaseRepository.save(token);
    }

    @Override
    public void updateToken(Token token) {
        couchbaseRepository.save(token);
    }

    @Override
    public Token selectTokenByToken(String token) {
        return couchbaseRepository.findById(token).orElse(null);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return couchbaseRepository
                .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                .findFirst()
                .orElse(null);
    }

}
