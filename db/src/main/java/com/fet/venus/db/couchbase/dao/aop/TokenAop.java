package com.fet.venus.db.couchbase.dao.aop;

import com.fet.venus.db.couchbase.config.CacheKey;
import com.fet.venus.db.couchbase.config.CouchbaseCacheConfig;
import com.fet.venus.db.dao.impl.TokenDAOJPAImpl;
import com.fet.venus.db.models.Token;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class TokenAop {

    @Autowired
    private TokenDAOJPAImpl tokenDAO;

    @Autowired
    private CouchbaseCacheConfig cacheConfig;

    @Pointcut("@annotation(tokenUpdate)")
    public void annotationPointCut(TokenUpdate tokenUpdate) {
    }

    @AfterReturning(pointcut = "annotationPointCut(tokenUpdate)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, TokenUpdate tokenUpdate, Object result) {

        if (!cacheConfig.isQueryCache(CacheKey.USE_TOKEN_CACHE)) {
            return;
        }

        Object[] args = joinPoint.getArgs();

        if (args[0] instanceof Token token) {
            String methodName = joinPoint.getSignature().getName();
            switch (methodName) {
                case "insertToken" -> {
                    log.info("afterReturning -- insertToken");
                    tokenDAO.insertToken(token);
                }
                case "updateToken" -> {
                    log.info("afterReturning -- updateToken");
                    tokenDAO.updateToken(token);
                }
            }
        }
    }
}
