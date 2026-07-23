package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.CustomerReplyDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLTRADIUSCUSTOMERREPLY")
@ApiModel(value = "Customer Reply",description = "This is Customer Reply entity which is used to update customer reply data")
public class CustomerReply 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "This is customer reply attribute id",required = true)
	@Column(name = "attributeid", nullable = false)
	private Long attributeId;
	
	@ApiModelProperty(notes = "This is customer id",required = true)
	@Column(name = "custid", nullable = false)
	private Long customerId;
	
	@ApiModelProperty(notes = "This is customer reply attribute",required = true)
	@Column(name = "attribute", nullable = false , length = 250)
	private String attribute;
	
	@ApiModelProperty(notes = "This is customer reply attribute value",required = true)
	@Column(name = "attributevalue", nullable = false , length = 250)
	private String attributeValue;
	
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

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId){
		this.mvnoId = mvnoId;
	}
	public Long getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
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
	
	public CustomerReply() 
	{
		super();
	}
	
	public CustomerReply(CustomerReplyDto customerReplyDto) 
	{
		this.customerId = customerReplyDto.getCustomerId();
		this.attribute = customerReplyDto.getAttribute();
		this.attributeValue = customerReplyDto.getAttributeValue();
	}
	
}
