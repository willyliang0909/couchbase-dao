package com.fet.venus.db.couchbase.repository;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.fet.venus.db.models.Token;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface TokenCouchbaseRepository extends CouchbaseRepository<Token, String> {

    @Query(value = "#{#n1ql.selectEntity} " +
            " where #{#n1ql.filter}" +
            " and fetToken = $fetToken" +
            " order by expireDateTime desc")
    List<Token> selectTokenByFetToken(String fetToken);

    List<Token> findAllByFetTokenOrderByExpireDateTimeDesc(String fetToken);
}

