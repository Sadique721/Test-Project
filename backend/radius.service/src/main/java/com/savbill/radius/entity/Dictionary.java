package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.DictionaryDto;
import com.savbill.radius.helper.UpdateDictionaryDto;
import com.savbill.radius.helper.VendorType;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLMDICTIONARY")
public class Dictionary 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated dictionary Id")
    @Column (name="dictionaryid", nullable = false)
	private Long dictionaryId;
	
	@ApiModelProperty(notes = "This is dictionary vendor")
    @Column (name="vendor", nullable = false, length = 250)
	private String vendor;
	
	@ApiModelProperty(notes = "This is dictionary vendor id")
    @Column (name="vendorid", nullable = false, length = 250)
	private String vendorId;
	
	@ApiModelProperty(notes = "This is dictionary vendor type")
    @Column (name="vendortype", nullable = false, length = 250)
	@Enumerated(EnumType.STRING)
	private VendorType vendorType;
	
	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;
	
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

//	@ApiModelProperty(hidden = true)
//	@Column (name="mvnoid", nullable = false, updatable = false)
//	private Integer mvnoId;

//	public Integer getMvnoId() {
//		return mvnoId;
//	}
//
//	public void setMvnoId(Integer mvnoId) {
//		this.mvnoId = mvnoId;
//	}

	public Long getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(Long dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public VendorType getVendorType() {
		return vendorType;
	}

	public void setVendorType(VendorType vendorType) {
		this.vendorType = vendorType;
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
	
	public Dictionary() {
	super();
	}
	
	public Dictionary(DictionaryDto dto)
	{
		this.vendor = dto.getVendor();
		this.vendorId = dto.getVendorId();
		this.vendorType = dto.getVendorType();
	}
	
	public Dictionary(UpdateDictionaryDto dto)
	{
		this.dictionaryId = dto.getDictionaryId();
		this.vendor = dto.getVendor();
		this.vendorId = dto.getVendorId();
		this.vendorType = dto.getVendorType();
	}
}
