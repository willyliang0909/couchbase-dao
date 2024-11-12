package com.fet.venus.db.couchbase.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.dao.CouchbaseCacheDao;
import com.fet.venus.db.dao.ILatestCacheRevampDAO;
import com.fet.venus.db.dao.impl.AbstractHibernateDaoImpl;
import com.fet.venus.db.dao.impl.LatestCacheRevampDAOJPAImpl;
import com.fet.venus.db.models.LatestCacheRevamp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Primary
@RequiredArgsConstructor
@Repository("latestCacheRevampCBDAO")
public class LatestCacheRevampCouchbaseDAOImpl extends AbstractHibernateDaoImpl<LatestCacheRevamp> implements ILatestCacheRevampDAO {

    private final CouchbaseCacheDao cacheDao;

    private final LatestCacheRevampDAOJPAImpl daojpa;

    @Override
    public List<LatestCacheRevamp> selectLatestList(byte nBufferType) {
        String cacheName = CacheKey.LATEST_REVAMP_CACHE;
        String key = String.valueOf(nBufferType);
        return cacheDao.get(
                CacheKey.USE_LATEST_REVAMP_CACHE,
                () -> cacheDao.get(cacheName, key, new TypeReference<>() {}),
                () -> daojpa.selectLatestList(nBufferType),
                entity -> cacheDao.save(cacheName, key, entity)
        ).orElse(new ArrayList<>());
    }
}
