package com.fet.venus.api.controller;

import com.fet.venus.db.dao.ITokenDAO;
import com.fet.venus.db.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private ITokenDAO tokenDAO;

    @PostMapping
    public Token insert(@RequestBody Token token) {
        tokenDAO.insertToken(token);
        return token;
    }

    @GetMapping("/{token}")
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
        return tokenDAO.selectTokenByFetToken(token);
    }

}
