package com.fet.venus.db.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "venus_latest_cache_revamp")
@IdClass(LatestCacheRevampId.class)
public class LatestCacheRevamp implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private int contentId;
    @Id
    private byte contentType;
    @Id
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
