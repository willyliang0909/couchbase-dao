package com.fet.venus.api.controller;

import com.fet.venus.db.couchbase.repository.LatestCacheRevampCouchbaseRepository;
import com.fet.venus.db.dao.ILatestCacheRevampDAO;
import com.fet.venus.db.models.LatestCacheRevamp;
import com.fet.venus.db.repository.LatestCacheRevampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/latest-revamp")
@RequiredArgsConstructor
public class LatestRevampController {

    private final ILatestCacheRevampDAO dao;

    private final LatestCacheRevampRepository repository;

    private final LatestCacheRevampCouchbaseRepository couchbaseRepository;

    @PostMapping("/jpa")
    public void insert(@RequestBody List<LatestCacheRevamp> revamp) {
        repository.saveAll(revamp);
    }

    @PostMapping("/couchbase")
    public void insertCouchbase(@RequestBody List<LatestCacheRevamp> revamp) {
        couchbaseRepository.saveAll(revamp);
    }

    @GetMapping("/{nBufferType}")
    public List<LatestCacheRevamp> test(@PathVariable byte nBufferType) {
        return dao.selectLatestList(nBufferType);
    }



}
