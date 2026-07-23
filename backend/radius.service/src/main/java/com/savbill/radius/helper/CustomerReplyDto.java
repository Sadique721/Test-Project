package com.savbill.radius.helper;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Customer Attribute Mapping",description = "This is data transfer object for Customer attribute mapping which is used to create new Customer attribute")
public class CustomerReplyDto {
	
	@ApiModelProperty(notes = "This is customer id")
	private Long customerId;
	
	@ApiModelProperty(notes = "This is customer reply attribute")
	private String attribute;
	
	@ApiModelProperty(notes = "This is customer reply attribute value")
	private String attributeValue;

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

}
