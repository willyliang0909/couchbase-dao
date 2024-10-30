package com.fet.venus.db.couchbase.dao.impl;

import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository("tokenCBDAO")
public class TokenCouchbaseDAOImpl implements ITokenDAO {

    @Autowired
    private TokenCouchbaseRepository repository;

    @Autowired
    private TokenDAOJPAImpl tokenDAOJPA;

    @Override
    public void insertToken(Token token) {
        repository.save(token);
    }

    @Override
    public void updateToken(Token token) {
        repository.save(token);
    }

    @Override
    public Token selectTokenByToken(String token) {
        return repository.findById(token).orElse(null);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return repository
                .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Token findOne(long id) {
        return null;
    }

    @Override
    public Token findOne(String id) {
        return null;
    }

    @Override
    public List<Token> findAll() {
        return List.of();
    }

    @Override
    public Token create(Token entity) {
        return null;
    }

    @Override
    public Token update(Token entity) {
        return null;
    }

    @Override
    public void delete(Token entity) {

    }
}
