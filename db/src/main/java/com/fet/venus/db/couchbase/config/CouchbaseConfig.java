package com.fet.venus.db.couchbase.config;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.UnambiguousTimeoutException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.codec.DefaultJsonSerializer;
import com.couchbase.client.java.codec.JsonTranscoder;
import com.couchbase.client.java.env.ClusterEnvironment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import java.time.Duration;

@Getter
@Setter
@Slf4j
@Configuration
@EnableCaching
@EnableCouchbaseRepositories(basePackages = {"com.fet.venus.db.couchbase.repository"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Value("${spring.couchbase.connection.string}")
    private String connectionString;

    @Value("${spring.couchbase.username}")
    private String userName;

    @Value("${spring.couchbase.password}")
    private String password;

    @Value("${spring.couchbase.bucket.name}")
    private String bucketName;

    @Value("${spring.couchbase.scope.name}")
    private String scopeName;

    @Override
    protected String getScopeName() {
        return StringUtils.isNotBlank(scopeName) ? scopeName : super.getScopeName();
    }

    @Override
    @Bean(destroyMethod = "disconnect")
    public Cluster couchbaseCluster(ClusterEnvironment couchbaseClusterEnvironment) {
        try {
            log.debug("Connecting to Couchbase cluster at {}", connectionString);
            Cluster cluster = Cluster.connect(connectionString, userName, password);
            cluster.waitUntilReady(Duration.ofSeconds(15));
            return cluster;
        } catch (UnambiguousTimeoutException e) {
            log.error("Connection to Couchbase cluster at {} timed out", connectionString);
            throw e;
        } catch (Exception e) {
            log.error(e.getClass().getName());
            log.error("Could not connect to Couchbase cluster at {}", connectionString);
            throw e;
        }
    }

    @Bean
    public Bucket getDefaultBucket(Cluster cluster) {
        try {
            if (!cluster.buckets().getAllBuckets().containsKey(getBucketName())) {
                log.error("Bucket with name {} does not exist. Creating it now", getBucketName());
                throw new BucketNotFoundException(bucketName);
            }
            return cluster.bucket(getBucketName());
        } catch (Exception e) {
            log.error("Error getting bucket", e);
            throw e;
        }
    }

    @Bean
    public CouchbaseCacheManager cacheManager(CouchbaseTemplate couchbaseTemplate) throws Exception {

//        JsonTranscoder jacksonTranscoder = JsonTranscoder.create(JacksonJsonSerializer.create());
//
//        var cacheConfiguration = CouchbaseCacheConfiguration.defaultCacheConfig()
//                .entryExpiry(Duration.ofMinutes(10)) // Example expiry duration
//                .disableCachingNullValues()
//                .valueTranscoder(jacksonTranscoder);
//
//        return CouchbaseCacheManager.builder(couchbaseTemplate.getCouchbaseClientFactory())
//                .cacheDefaults(cacheConfiguration)
//                .build();

        JsonTranscoder jsonTranscoder = JsonTranscoder.create(DefaultJsonSerializer.create());

//        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager.CouchbaseCacheManagerBuilder
//                .fromConnectionFactory(couchbaseTemplate.getCouchbaseClientFactory());
        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager
                .builder(couchbaseTemplate.getCouchbaseClientFactory().withScope("auth"));

        builder.withCacheConfiguration("token", CouchbaseCacheConfiguration
                .defaultCacheConfig()
                .computePrefixWith(cacheName -> "")
                .valueTranscoder(jsonTranscoder)
                .collection("token")
        );
        return builder.build();
    }
}
