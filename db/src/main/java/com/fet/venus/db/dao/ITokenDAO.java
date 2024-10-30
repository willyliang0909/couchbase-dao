package com.fet.venus.db.dao;

import com.fet.venus.db.models.Token;

import java.util.List;

public interface ITokenDAO extends IOperations<Token> {
    
    void insertToken(Token token);
    void updateToken(Token token);
    Token selectTokenByToken(String token);
    Token selectTokenByFetToken(String fetToken);
}
