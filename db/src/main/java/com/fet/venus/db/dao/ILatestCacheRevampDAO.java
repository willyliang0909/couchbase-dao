package com.fet.venus.db.dao;

import com.fet.venus.db.models.LatestCacheRevamp;

import java.util.List;

public interface ILatestCacheRevampDAO extends IOperations<LatestCacheRevamp> {
    List<LatestCacheRevamp> selectLatestList(byte nBufferType);
}