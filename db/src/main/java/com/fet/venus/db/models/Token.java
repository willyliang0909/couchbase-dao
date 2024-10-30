package com.fet.venus.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "venus_token")
public class Token implements Serializable {

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

	public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public String getScope() {
	return scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    public Integer getMemberId() {
	return memberId;
    }

    public void setMemberId(Integer memberId) {
	this.memberId = memberId;
    }

    public String getFetToken() {
	return fetToken;
    }

    public void setFetToken(String fetToken) {
	this.fetToken = fetToken;
    }

    public String getPlatform() {
	return platform;
    }

    public void setPlatform(String platform) {
	this.platform = platform;
    }

    public String getUdid() {
	return udid;
    }

    public void setUdid(String udid) {
	this.udid = udid;
    }

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public String getUserAgent() {
	return userAgent;
    }

    public void setUserAgent(String userAgent) {
	this.userAgent = userAgent;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getSerialNumber() {
	return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
    }

    public Date getEffectiveDateTime() {
	return effectiveDateTime;
    }

    public void setEffectiveDateTime(Date effectiveDateTime) {
	this.effectiveDateTime = effectiveDateTime;
    }

    public Date getExpireDateTime() {
	return expireDateTime;
    }

    public void setExpireDateTime(Date expireDateTime) {
	this.expireDateTime = expireDateTime;
    }

	public String getSdpRefreshToken() {
		return sdpRefreshToken;
	}

	public void setSdpRefreshToken(String sdpRefreshToken) {
		this.sdpRefreshToken = sdpRefreshToken;
	}
	
	public Byte getLoginType() {
		return loginType;
	}
	
	public void setLoginType(Byte loginType) {
		this.loginType = loginType;
	}
}
