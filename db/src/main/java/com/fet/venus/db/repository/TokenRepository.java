package com.fet.venus.db.repository;

import com.fet.venus.db.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {

    @Query(value = "from Token t" +
            " where t.fetToken = :fetToken" +
            " order by t.expireDateTime desc")
    List<Token> selectTokenByFetToken(String fetToken);
}
