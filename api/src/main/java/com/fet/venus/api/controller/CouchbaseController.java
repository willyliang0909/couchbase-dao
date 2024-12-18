package com.fet.venus.api.controller;

import com.fet.venus.db.couchbase.dao.impl.TokenCouchbaseDAOImpl;
import com.fet.venus.db.models.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couchbase")
@RequiredArgsConstructor
public class CouchbaseController {

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
}
