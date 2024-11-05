package com.fet.venus.db.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class LatestCacheRevampId implements Serializable {
    private int contentId;
    private byte contentType;
    private byte bufferType;
}
