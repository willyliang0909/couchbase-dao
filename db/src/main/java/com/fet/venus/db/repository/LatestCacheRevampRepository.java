package com.fet.venus.db.repository;

import com.fet.venus.db.models.LatestCacheRevamp;
import com.fet.venus.db.models.LatestCacheRevampId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LatestCacheRevampRepository extends JpaRepository<LatestCacheRevamp, LatestCacheRevampId> {

    List<LatestCacheRevamp> findAllByBufferType(byte contentType);
}
