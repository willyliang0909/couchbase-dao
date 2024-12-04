package com.fet.venus.db.couchbase.models;

import com.fet.venus.db.models.LatestCacheRevamp;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document
@Collection("venus_latest_cache_revamp_queue")
public class LatestCacheRevampQueue implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    public String id;

    public LocalDateTime publishDateTime;

    @NotEmpty
    public List<LatestCacheRevamp> data;

}
