package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Dictionary Value Update",description = "This is data transfer object for Dictionary Value which is used to update dictionary value data")
public class UpdateDictionaryValueDto 
{
	@ApiModelProperty(notes = "Dictioanry value id to update data",required=true)
	private Long dictionaryValueId;
	@ApiModelProperty(notes = "Name of the dictionary value",required=true)
	private String name;
	@ApiModelProperty(notes = "Value of the dictionary",required=true)
	private String value;
	@ApiModelProperty(notes = "Name of the dictionary attribute",required=true)
	private String dictionaryAttributeName;
	@ApiModelProperty(notes = "Id of the dictionary attribute",required=true)
	private Long dictionaryAttributeId;

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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDictionaryAttributeName() {
		return dictionaryAttributeName;
	}
	public void setDictionaryAttributeName(String dictionaryAttributeName) {
		this.dictionaryAttributeName = dictionaryAttributeName;
	}
	public Long getDictionaryAttributeId() {
		return dictionaryAttributeId;
	}

	public void setDictionaryAttributeId(Long dictionaryAttributeId) {
		this.dictionaryAttributeId = dictionaryAttributeId;
	}
}
