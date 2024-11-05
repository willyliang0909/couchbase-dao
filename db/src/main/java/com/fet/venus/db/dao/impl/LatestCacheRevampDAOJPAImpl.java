package com.fet.venus.db.dao.impl;

import com.fet.venus.db.dao.ILatestCacheRevampDAO;
import com.fet.venus.db.models.LatestCacheRevamp;
import com.fet.venus.db.repository.LatestCacheRevampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("latestCacheRevampDAO")
@RequiredArgsConstructor
public class LatestCacheRevampDAOJPAImpl extends AbstractHibernateDaoImpl<LatestCacheRevamp> implements ILatestCacheRevampDAO {

    private final LatestCacheRevampRepository repository;

    @Override
    public List<LatestCacheRevamp> selectLatestList(byte nBufferType) {
        return repository.findAllByBufferType(nBufferType);
    }
}
