package com.fet.venus.db.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdAttribute;
import org.springframework.data.couchbase.repository.Collection;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "venus_token")
@Document
@Collection("venus_token")
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Transient
    @JsonIgnore
    private String couchbaseId;

    @jakarta.persistence.Id
    @IdAttribute
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
