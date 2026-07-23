package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.radius.helper.AttributeCategory;
import com.savbill.radius.helper.DictionaryAttributeDto;
import com.savbill.radius.helper.UpdateDictionaryAttributeDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryInit;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLTDICTIONARYATTRIBUTE")
public class DictionaryAttribute {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated dictionary attribute Id")
    @Column (name="dictionaryattributeid", nullable = false)
	private Long dictionaryAttributeId;
	
	@ApiModelProperty(notes = "This is dictionary attribute name")
    @Column (name="name", nullable = false, length = 250)
	private String name;
	
	@ApiModelProperty(notes = "This is dictionary attribute category")
    @Column (name="category", nullable = false, length = 250)
	@Enumerated(EnumType.STRING)
	private AttributeCategory category;
	
	@ApiModelProperty(notes = "This is dictionary attribute type")
    @Column (name="type", nullable = false, length = 250)
	private String type;
	
	@ApiModelProperty(notes = "This is attribute id")
    @Column (name="attributeid", nullable = false, length = 250)
	private String attributeId;
	
	@ApiModelProperty(notes = "This is dictionary id")
	@JoinColumn(name="dictionaryid")
	@ManyToOne(optional = false)
	@QueryInit("dictionaryId")
	private Dictionary dictionary;
	
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
//
//	public Integer getMvnoId() {
//		return mvnoId;
//	}
//
//	public void setMvnoId(Integer mvnoId) {
//		this.mvnoId = mvnoId;
//	}

	public Long getDictionaryAttributeId() {
		return dictionaryAttributeId;
	}

	public void setDictionaryAttributeId(Long dictionaryAttributeId) {
		this.dictionaryAttributeId = dictionaryAttributeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttributeCategory getCategory() {
		return category;
	}

	public void setCategory(AttributeCategory category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

//	public Long getDictionaryId() {
//		return dictionaryId;
//	}
//
//	public void setDictionaryId(Long dictionaryId) {
//		this.dictionaryId = dictionaryId;
//	}
	
	
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
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

	public DictionaryAttribute() {
		super();
	}
	
	public DictionaryAttribute(DictionaryAttributeDto dto, Dictionary dictionary)
	{
		this.attributeId = dto.getAttributeId();
		this.category = dto.getCategory();
		this.dictionary = dictionary;
		this.name=dto.getName();
		this.type=dto.getType();
	}
	
	public DictionaryAttribute(UpdateDictionaryAttributeDto dto, Dictionary dictionary)
	{
		this.attributeId = dto.getAttributeId();
		this.category = dto.getCategory();
		this.dictionary = dictionary;
		this.name=dto.getName();
		this.type=dto.getType();
		this.dictionaryAttributeId=dto.getDictionaryAttributeId();
	}
}
