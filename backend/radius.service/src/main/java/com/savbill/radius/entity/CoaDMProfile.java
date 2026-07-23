package com.savbill.radius.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.savbill.radius.helper.CoaDMProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLTCOADMPROFILE")
@ApiModel(value = "COA DM Profile Entity",description = "This is COA/DM Profile entity which is used to update COA/DM Profile data")
public class CoaDMProfile
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated COA/DM Profile Id")
    @Column (name="coadmprofileid", nullable = false)
	private Long coaDMProfileId;

	@ApiModelProperty(notes = "This is COA/DM Profile Name")
    @Column (name="name", nullable = false , length = 250)
    private String name;

	@ApiModelProperty(notes = "Gateway of the COA/DM Profile")
    @Column (name="gateway", nullable = true , length = 15)
    private String gateway;

	@ApiModelProperty(notes = "Shared Key of the COA/DM Profile")
    @Column (name="sharedkey", nullable = false , length = 15)
    private String sharedkey;

	@ApiModelProperty(notes = "Port of the COA/DM Profile")
    @Column (name="port", nullable = false , length = 15)
    private Integer port;
	
	@ApiModelProperty(notes = "Type of the COA/DM Profile")
    @Column (name="type", nullable = false , length = 25)
    private String type;

	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;

	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@Column(name = "timevar", length = 4)
	private Double timevar;

//	@Column(name = "unitsoftime",nullable = false, length = 40, columnDefinition = "varchar(100) default 'Hours'")
//	private String unitsOftime;
	
	@Transient
    List<CoaDMProfileAttribute> coaDMProfileAttributeList;

	
	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Long getCoaDMProfileId() {
		return coaDMProfileId;
	}

	public void setCoaDMProfileId(Long coaDMProfileId) {
		this.coaDMProfileId = coaDMProfileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getSharedkey() {
		return sharedkey;
	}

	public void setSharedkey(String sharedkey) {
		this.sharedkey = sharedkey;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public Timestamp getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setLastModifiedOn(Timestamp lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public Double getTimevar() {
		return timevar;
	}

	public void setTimevar(Double timevar) {
		this.timevar = timevar;
	}

//	public String getUnitsOftime() {
//		return unitsOftime;
//	}
//
//	public void setUnitsOftime(String unitsOftime) {
//		this.unitsOftime = unitsOftime;
//	}

	public CoaDMProfile() {
		super();
	}
	
	public List<CoaDMProfileAttribute> getCoaDMProfileAttributeList() {
		return coaDMProfileAttributeList;
	}

	public void setCoaDMProfileAttributeList(List<CoaDMProfileAttribute> coaDMProfileAttributeList) {
		this.coaDMProfileAttributeList = coaDMProfileAttributeList;
	}

	public CoaDMProfile(CoaDMProfileDto coaDMProfileDto) {
		this.name = coaDMProfileDto.getName();
		this.gateway = coaDMProfileDto.getGateway();
		this.sharedkey = coaDMProfileDto.getSharedkey();
		this.port = coaDMProfileDto.getPort();
		this.type = coaDMProfileDto.getType();
		this.timevar = coaDMProfileDto.getTimevar();
//		this.unitsOftime = coaDMProfileDto.getUnitsOftime();
	}
}
