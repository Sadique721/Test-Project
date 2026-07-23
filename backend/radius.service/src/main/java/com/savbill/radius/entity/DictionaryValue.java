package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.radius.helper.DictionaryValueDto;
import com.savbill.radius.helper.UpdateDictionaryValueDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryInit;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLTDICTIONARYVALUE")
public class DictionaryValue {
	
	private static final String VALUE = "VALUE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated dictionary value id")
    @Column (name="dictionaryvalueid", nullable = false)
	private Long dictionaryValueId;
	
	@ApiModelProperty(notes = "This is dictionary value name")
    @Column (name="name", nullable = false, length = 250)
	private String name;
	
	@ApiModelProperty(notes = "This is dictionary value type")
    @Column (name="type", nullable = false, length = 250)
	private String type;
	
	@ApiModelProperty(notes = "This is dictionary value")
    @Column (name="value", nullable = false, length = 250)
	private String value;
	
	@ApiModelProperty(notes = "This is dictionary attribute id")
	@JoinColumn(name="dictionaryattributeid")
	@ManyToOne(optional = false)
	@QueryInit("dictionaryAttributeId")
	private DictionaryAttribute dictionaryAttribute;
	
	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;
	
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

//	@ApiModelProperty(hidden = true)
//	@Column (name="mvnoid", nullable = false)
//	private Integer mvnoId;

//	public Integer getMvnoId() {
//		return mvnoId;
//	}
//
//	public void setMvnoId(Integer mvnoId) {
//		this.mvnoId = mvnoId;
//	}

	public Long getDictionaryValueId() {
		return dictionaryValueId;
	}

	public void setDictionaryValueId(Long dictionaryValueId) {
		this.dictionaryValueId = dictionaryValueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	public DictionaryAttribute getDictionaryAttribute() {
		return dictionaryAttribute;
	}

	public void setDictionaryAttribute(DictionaryAttribute dictionaryAttribute) {
		this.dictionaryAttribute = dictionaryAttribute;
	}

	public DictionaryValue() {
		super();
	}
	
	public DictionaryValue(DictionaryValueDto dto, DictionaryAttribute dicAttrVo)
	{
		this.dictionaryAttribute = dicAttrVo;
		this.name = dto.getName();
		this.type = VALUE;
		this.value = dto.getValue();
	}

	public DictionaryValue(UpdateDictionaryValueDto dto, DictionaryAttribute dicAttrVo)
	{
		this.dictionaryAttribute = dicAttrVo;
		this.name = dto.getName();
		this.type = VALUE;
		this.value = dto.getValue();
		this.dictionaryValueId= dto.getDictionaryValueId();
	}
	
}
