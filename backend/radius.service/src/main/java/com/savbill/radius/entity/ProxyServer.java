package com.savbill.radius.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.ProxyServerDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLTPROXYSERVER")
@JsonInclude(Include.NON_NULL)
public class ProxyServer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROXYSERVERID", nullable = false)
	private Long id;
	
	@Column(name = "NAME", length = 100)
	private String name;
	
	@Column(name = "ip", length = 100)
	private String ip;

	@Column(name = "authport", length = 6)
	private String authport;

	@Column(name = "acctport", length = 6)
	private String acctport;

	@Column(name = "secretkey", length = 100)
	private String secretkey;
	
	@Column(name = "status", length = 10)
	private String status;

	@Column (name="CREATEDATE")
	@JsonProperty("createDate")
	private ZonedDateTime createdon;

	@Column (name="LASTMODIFICATIONDATE")
	@JsonProperty("lastModificationDate")
	private ZonedDateTime lastmodifiedon;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@Column(name = "dyna_auth_port")
	@JsonProperty("dynaAuthPort")
	private String dynaAuthPort;
	
	@Column(name = "override_nas")
	@JsonProperty("overrideNAS")
	private Boolean overrideNAS;
	
	@Column(name = "nasip", length = 25)
	@JsonProperty("nasip")
	private String nasip;

	@Column(name = "timeout", length = 10)
	@JsonProperty("timeout")
	private String timeout;

	
	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAuthport() {
		return authport;
	}

	public void setAuthport(String authport) {
		this.authport = authport;
	}

	public String getAcctport() {
		return acctport;
	}

	public void setAcctport(String acctport) {
		this.acctport = acctport;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ZonedDateTime getCreatedon() {
		return createdon;
	}

	public void setCreatedon(ZonedDateTime createdon) {
		this.createdon = createdon;
	}

	public ZonedDateTime getLastmodifiedon() {
		return lastmodifiedon;
	}

	public void setLastmodifiedon(ZonedDateTime lastmodifiedon) {
		this.lastmodifiedon = lastmodifiedon;
	}
	
	

	public String getDynaAuthPort() {
		return dynaAuthPort;
	}

	public void setDynaAuthPort(String dynaAuthPort) {
		this.dynaAuthPort = dynaAuthPort;
	}

	public Boolean getOverrideNAS() {
		return overrideNAS;
	}

	public void setOverrideNAS(Boolean overrideNAS) {
		this.overrideNAS = overrideNAS;
	}
	
	

	public String getNasip() {
		return nasip;
	}

	public void setNasip(String nasip) {
		this.nasip = nasip;
	}
	
	

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public ProxyServer(Long id, String name, String ip, String authport, String acctport, String secretkey,
			String status, ZonedDateTime createdon, ZonedDateTime lastmodifiedon,String dynaAuthPort,Boolean overrideNAS,String nasip,String timeout) {
		super();
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.authport = authport;
		this.acctport = acctport;
		this.secretkey = secretkey;
		this.status = status;
		this.createdon = createdon;
		this.lastmodifiedon = lastmodifiedon;
		this.dynaAuthPort=dynaAuthPort;
		this.overrideNAS=overrideNAS;
		this.nasip=nasip;
		this.timeout=timeout;
	}
	
	
	public ProxyServer(ProxyServerDto dto) {
		this.name = dto.getName();
		this.ip = dto.getIp();
		this.authport = dto.getAuthport();
		this.acctport = dto.getAcctport();
		this.secretkey = dto.getSecretkey();
		this.status = dto.getStatus();
		this.dynaAuthPort=dto.getDynaAuthPort();
		this.overrideNAS=dto.getOverrideNAS();
		this.nasip=dto.getNasip();
		this.timeout=dto.getTimeout();
	}

	public ProxyServer() {
		super();
	}
	
	
	
}
