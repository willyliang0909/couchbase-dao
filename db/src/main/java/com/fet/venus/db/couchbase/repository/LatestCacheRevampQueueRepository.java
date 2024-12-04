package com.fet.venus.db.couchbase.repository;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.fet.venus.db.couchbase.models.LatestCacheRevampQueue;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.stereotype.Repository;

@Repository
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface LatestCacheRevampQueueRepository extends CouchbaseRepository<LatestCacheRevampQueue, String> {
}