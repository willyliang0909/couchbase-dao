package com.fet.venus.db.dao.impl;

import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.models.Token;
import com.fet.venus.db.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@RequiredArgsConstructor
@Repository("tokenDBDAO")
public class TokenDAOJPAImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    private final TokenRepository repository;

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
        return repository.selectTokenByFetToken(fetToken).stream().findFirst().orElse(null);
    }
}
