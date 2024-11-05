package com.fet.venus.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Document
@Collection("token")
@Entity
@Table(name = "venus_token")
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.annotation.Id
    private String token;
    private String scope;
    private Integer memberId;
    private String fetToken;
    private String udid;
    private String platform;
    private String model;
    private String version;
    private String userAgent;
    private String code;
    private String serialNumber;
    private Date effectiveDateTime;
    private Date expireDateTime;
    @Column(name = "refresh_token")
    private String sdpRefreshToken; // added: 2020-04-01. Migration to 靠近.
    private Byte loginType;         // added: 2020-04-07. Migration to 靠近.

}
