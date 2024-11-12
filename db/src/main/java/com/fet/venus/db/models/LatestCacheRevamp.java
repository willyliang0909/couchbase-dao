package com.fet.venus.db.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdAttribute;
import org.springframework.data.couchbase.repository.Collection;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "venus_latest_cache_revamp")
@IdClass(LatestCacheRevampId.class)
@Document
@Collection("venus_latest_cache_revamp")
public class LatestCacheRevamp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @org.springframework.data.annotation.Id
    @GeneratedValue(delimiter = "::")
    @Transient
    @JsonIgnore
    private String couchbaseId;

    @Id
    @IdAttribute(order = 0)
    private int contentId;
    @Id
    @IdAttribute(order = 1)
    private byte contentType;
    @Id
    @IdAttribute(order = 2)
    private byte bufferType;

    private String chineseName;
    private String englishName;
    private String duration;
    private double fridayScore;
    private int scoreCount;
    private Date effectiveDateTime;
    private Date expireDateTime;
    private String updateEpisode;
    private int totalEpisode;
    private String area;
    private int year;
    private int orderId;
    private String imageUrl;
    private String paymentTag;
    private String propertyTag;
    private boolean isEmpty;
    private byte rating;
    private Boolean isSexual;
    private String introduction;
    private String themeImageUrl;
    private int trailerContentId;
    private int trailerStreamingId;
    private int trailerDuration;

}
