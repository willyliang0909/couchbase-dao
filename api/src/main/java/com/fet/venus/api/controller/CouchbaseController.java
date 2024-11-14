package com.fet.venus.api.controller;

import com.fet.venus.db.couchbase.dao.impl.TokenCouchbaseDAOImpl;
import com.fet.venus.db.couchbase.repository.TokenCouchbaseRepository;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couchbase")
@RequiredArgsConstructor
public class CouchbaseController {

    private final CouchbaseTemplate couchbaseTemplate;

    private final TokenCouchbaseRepository repository;

    @PostMapping("/token")
    public Token insertCouchbase(@RequestBody Token token) {
        return repository.save(token);
    }

    @GetMapping("/token/{token}")
    public Token insertCouchbase(@PathVariable String token) {
        return repository.findById(token).orElse(null);
    }

    @PostMapping("/token/template")
    public Token insertBy(@RequestBody Token token) {
        return couchbaseTemplate.save(token);
        //return couchbaseTemplate.insertById(Token.class).one(token);
    }


    @GetMapping("/fetToken/{fetToken}")
    public Token select(@PathVariable String fetToken) {
        return repository.findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream().findFirst().orElse(null);
    }

}
