# couchbase dao and caching

>couchbase cache 可以由以下幾種方式完成,
> 主要還是透過couchbase 本身 memory first的儲存機制
> 1. spring @Cacheable, cacheManager
> 2. select object by key
> 3. use sql++ with index

**env**
```properties
Spring boot: 3.2.11
Spring framework: 6.1.14
Spring data: 3.2.11
Spring data couchbase: 5.2.11
```

## 實作方式
| function              | cache方式   | 實作方式                 |
|-----------------------|-----------|----------------------|
| insertToken           | key value | Repository           |
| updateToken           | key value | Repository           |
| selectTokenByToken    | key value | Repository           |
| selectTokenByFetToken | index     | Repository, sql++    |
| selectLatestList      | key value | Repository, eventing |

selectLatestList
> 使用repository更新LatestCacheRevamp時觸發eventing, 重新查詢後更新cache collection

### 1. connection configuration

**properties**
```properties
spring.couchbase.connection.string=${COUCHBASE_CONNECTION_STRING:localhost}
spring.couchbase.username=${COUCHBASE_USERNAME:Administrator}
spring.couchbase.password=${COUCHBASE_PASSWORD:couchbase}
spring.couchbase.bucket.name=${COUCHBASE_BUCKET_NAME:vod}
spring.couchbase.scope.name=${COUCHBASE_SCOPE_NAME:friday}
```

<br>

**CouchbaseConfig**

設定couchbase連線資訊, 後續就可以使用
couchbaseTemplate 及 CouchbaseRepository
進行couchbase操作


```java
@Getter
@Setter
@Slf4j
@Configuration
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

}
```

<br>

### 2. cache configuration

1. 設定cacheManager bean
2. default config
3. 依照不同cache需求設定不同cache config

```java
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CouchbaseCacheConfig {

    @Bean
    public CouchbaseCacheManager cacheManager(CouchbaseTemplate template) {

        JsonTranscoder jsonTranscoder = JsonTranscoder.create(JacksonJsonSerializer.create());

        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager
                .builder(template.getCouchbaseClientFactory().withScope("cache"))
                .cacheDefaults(CouchbaseCacheConfiguration
                        .defaultCacheConfig()
                        .disableCachingNullValues()
                        .valueTranscoder(jsonTranscoder)
                        .collection("data")
                );

        builder.withCacheConfiguration(CacheKey.BINARY_CACHE,
                CouchbaseCacheConfiguration
                        .defaultCacheConfig()
                        .disableCachingNullValues()
                        .collection("data")
        );

        return builder.build();
    }
} 
```

<br>

### 3. cache test - Cacheable 
```java
@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheTest {
    private final TestUserService testUserService;

    @GetMapping("/binary/{key}/{value}")
    @Cacheable(value = CacheKey.BINARY_CACHE, key = "#key")
    public String cacheBinary(@PathVariable String key, @PathVariable String value) {
        log.info("return key: {}, value: {}", key, value);
        return value;
    }

    @GetMapping("/binary/testuser")
    @Cacheable(value = CacheKey.BINARY_CACHE, key = "'binaryTestUser'")
    public TestUser binaryTestUser() {
        log.info("new TestUser");
        return testUserService.newUser("user", 40L);
    }
}
```
```shell
curl --location 'http://localhost:8080/cache/binary/key1/value'
curl --location 'http://localhost:8080/cache/binary/testuser'
```
![cache.image](/images/cache.png)


### 4. model configuration 
- @Document (類似@Entity)
- @Collection("venus_token") (類似@Table)
- @Id @GeneratedValue, 如果設定GeneratedValue, 預設會由@IdAttribute產生,
- 若沒有則是直接使用此欄位的值
```java


@Setter
@Getter
@Document
@Collection("venus_token")
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private String couchbaseId;

    @IdAttribute
    private String token;
    private String scope;
    private Integer memberId;
    //...
}
```


### 5. CouchbaseRepository 
```java
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
```

<br>

### 6. dao test 
```java
@RestController
@RequestMapping("/couchbase")
@RequiredArgsConstructor
public class CouchbaseController {

    private final CouchbaseTemplate couchbaseTemplate;

    private final TokenCouchbaseRepository repository;

    @PostMapping("/token")
    public Token insertCouchbase(@RequestBody Token token) {
        return repository.save(token);
    }

    @GetMapping("/token/{token}")
    public Token insertCouchbase(@PathVariable String token) {
        return repository.findById(token).orElse(null);
    }

    @PostMapping("/token/template")
    public Token insertBy(@RequestBody Token token) {
        return couchbaseTemplate.save(token);
        //return couchbaseTemplate.insertById(Token.class).one(token);
    }


    @GetMapping("/fetToken/{fetToken}")
    public Token select(@PathVariable String fetToken) {
        return repository.findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream().findFirst().orElse(null);
    }

}
```
```shell
curl --location 'http://localhost:8080/couchbase/token/template' \
--header 'Content-Type: application/json' \
--data '{
    "token": "mz6iXcgyxlfzH6gZzhXomdZJiBhK5hmR",
    "effectiveDateTime": "2024-10-30T05:41:23.473+00:00",
    "expireDateTime": "2024-10-30T05:41:23.473+00:00",
    "platform": "PC Web",
    "model": "Chrome 12(PC)",
    "scope": "member",
    "version": "124.0.0.1",
    "udid": "1719300035961",
    "fetToken": "h4J4iEeilsuaCwsJPceM"
}'
```
![document.image](/images/document.png)
```shell
curl --location 'http://localhost:8080/couchbase/token/mz6iXcgyxlfzH6gZzhXomdZJiBhK5hmR'
curl --location 'http://localhost:8080/couchbase/fetToken/h4J4iEeilsuaCwsJPceM'
```


### 7. cache dao
>由於Cacheable不完全符合所需流程, 因此自定義cache dao

**GET**
```java
public <T> Optional<T> get(
            String userCacheKey,
            Supplier<Optional<T>> cacheFunction,
            Supplier<T> getFunction,
            Consumer<T> cacheSaveFunction
    ) {
        if (cacheConfig.isQueryCache(userCacheKey)) {
            log.info("from cache");
            Optional<T> cacheResult = cacheFunction.get();
            if (cacheResult.isPresent()) {
                log.info("found in cache");
                return cacheResult;
            } else {
                log.info("is not in cache, search from db");
                T getResult = getFunction.get();
                if (getResult != null) {
                    log.info("found in db");
                    cacheSaveFunction.accept(getResult);
                    log.info("saved to cache");
                }
                return Optional.ofNullable(getResult);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("cache is disabled");
            }
            return Optional.ofNullable(getFunction.get());
        }
    }
```
usage
```java
@Override
    public Token selectTokenByToken(String token) {
        return cacheDao.get(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository.findById(token),
                () -> tokenDAOJPA.selectTokenByToken(token),
                couchbaseRepository::save
        ).orElse(null);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return cacheDao.get(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository
                        .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                        .findFirst(),
                () -> tokenDAOJPA.selectTokenByFetToken(fetToken),
                couchbaseRepository::save
        ).orElse(null);
    }
```

**SAVE**
```java
public <T> T save(
            String userCacheKey,
            T entity,
            Function<T, T> cacheFunction,
            Function<T, T> saveFunction
    ){
        if (cacheConfig.isQueryCache(userCacheKey)) {
            log.info("save to cache");
            saveAsync(entity, saveFunction);
            return cacheFunction.apply(entity);
        } else {
            return saveFunction.apply(entity);
        }
    }

    protected <T> void saveAsync(T entity, Function<T, T> saveFunction) {
        log.info("save to db");
        CompletableFuture
                .runAsync(() -> {
                    log.info("saving to db");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    saveFunction.apply(entity);
                    log.info("saved to db");
                });
    }
```
usage
```java 
@Override
    public void insertToken(Token token) {
        cacheDao.save(
                CacheKey.USE_TOKEN_CACHE,
                token,
                couchbaseRepository::save,
                t -> {
                    tokenDAOJPA.insertToken(t);
                    return t;
                }
        );
    }
```

### 8. eventing
> 由於LatestCacheRevamp資料量小, 因此直接使用function中的參數作為key直接查詢, 加快速度,
> 設計上使用兩個collection: friday.venus_latest_cache_revamp, cache.data
> 當venus_latest_cache_revamp資料變更時, 重新查詢select r from LatestCacheRevamp r where bufferType = :bufferType order by orderId asc
> 並將結果存入cache.data



```shell
function OnUpdate(doc, meta, xattrs) {
    log("Doc created/updated", meta.id);
    refreshCache(doc);
}

function OnDelete(meta, options) {
    log("Doc deleted/expired", meta.id);
}

function refreshCache(doc) {
    const _class = 'com.fet.venus.db.models.LatestCacheRevamp';
    const buffer_type = doc.bufferType;

    if ( doc._class != _class ) {
        return;
    }

    const query =
        'SELECT revamp.* ' +
        'FROM `vod`.`friday`.`venus_latest_cache_revamp` revamp ' +
        'WHERE _class = $1 AND bufferType = $2 ' +
        'ORDER BY revamp.`orderId` ASC';

    const results = N1QL(query, [_class, buffer_type], { isPrepared: true });

    const cache_array = []
    for (var item of results) {   // Stream results using 'for' iterator.
        cache_array.push(item)
    }
    results.close();

    const cache_key = `latest_revamp::${buffer_type}`
    cache_bucket[cache_key] = cache_array
}
```


## implementation
ILatestCacheRevampDAO
```java
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

```

ITokenDAO
```java
@Primary
@RequiredArgsConstructor
@Repository("tokenCBDAO")
public class TokenCouchbaseDAOImpl extends AbstractHibernateDaoImpl<Token> implements ITokenDAO {

    private final TokenCouchbaseRepository couchbaseRepository;

    private final TokenDAOJPAImpl tokenDAOJPA;

    private final CouchbaseCacheDao cacheDao;

    @Override
    public void insertToken(Token token) {
        cacheDao.save(
                CacheKey.USE_TOKEN_CACHE,
                token,
                couchbaseRepository::save,
                t -> {
                    tokenDAOJPA.insertToken(t);
                    return t;
                }
        );
    }

    @Override
    public void updateToken(Token token) {
        cacheDao.save(
                CacheKey.USE_TOKEN_CACHE,
                token,
                couchbaseRepository::save,
                t -> {
                    tokenDAOJPA.insertToken(t);
                    return t;
                }
        );
    }

    @Override
    public Token selectTokenByToken(String token) {
        return cacheDao.get(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository.findById(token),
                () -> tokenDAOJPA.selectTokenByToken(token),
                couchbaseRepository::save
        ).orElse(null);
    }

    @Override
    public Token selectTokenByFetToken(String fetToken) {
        return cacheDao.get(
                CacheKey.USE_TOKEN_CACHE,
                () -> couchbaseRepository
                        .findAllByFetTokenOrderByExpireDateTimeDesc(fetToken).stream()
                        .findFirst(),
                () -> tokenDAOJPA.selectTokenByFetToken(fetToken),
                couchbaseRepository::save
        ).orElse(null);
    }

}

```