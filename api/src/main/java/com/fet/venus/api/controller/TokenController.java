package com.fet.venus.api.controller;

import com.fet.venus.db.couchbase.dao.impl.TokenCouchbaseDAOImpl;
import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final ITokenDAO tokenDAO;

    private final TokenCouchbaseDAOImpl couchbaseDAO;

    private final CouchbaseTemplate couchbaseTemplate;

    @PostMapping("/couchbase")
    public void insertCouchbase(@RequestBody Token token) {
        couchbaseDAO.insertToken(token);
    }

    @PostMapping("/template")
    public Token insertBy(@RequestBody Token token) {
        return couchbaseTemplate.save(token);
        //return couchbaseTemplate.insertById(Token.class).one(token);
    }

    @PostMapping
    public Token insert(@RequestBody Token token) {
        tokenDAO.insertToken(token);
        return token;
    }


    @GetMapping("/{token}")
    @Cacheable(value = "token")
    public Token selectTokenByToken(@PathVariable String token) {
        return tokenDAO.selectTokenByToken(token);
    }

    @PutMapping
    public Token update(@RequestBody Token token) {
        tokenDAO.updateToken(token);
        return token;
    }

    @GetMapping("/fetToken/{token}")
    public Token selectTokenByFetToken(@PathVariable String token) {
        long start = System.currentTimeMillis();
        var t =  tokenDAO.selectTokenByFetToken(token);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return t;
    }

}
