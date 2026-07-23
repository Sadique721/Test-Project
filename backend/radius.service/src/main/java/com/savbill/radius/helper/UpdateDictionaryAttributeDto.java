package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Dictionary Attribute Update",description = "This is data transfer object for Dictionary Attribute which is used to update dictionary attribute data")
public class UpdateDictionaryAttributeDto 
{
	@ApiModelProperty(notes = "Dictionary attribute id to update data",required=true)
	private Long dictionaryAttributeId;
	@ApiModelProperty(notes = "Name of the dictionary attribute",required=true)
	private String name;
	@ApiModelProperty(notes = "Categories of attribute",allowableValues = "ATTRIBUTE,VENDORATTR",  value = "This field accept value only : ATTRIBUTE or VENDORATTR",required = true)
	private AttributeCategory category;
	@ApiModelProperty(notes = "Type of the dictionary attribute",required=true)
	private String type;
	@ApiModelProperty(notes = "This is attribute id",required=true)
	private String attributeId;
	@ApiModelProperty(notes = "Name of the vendor",required=true)
	private String vendor;
	
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
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
}
