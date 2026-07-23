package com.savbill.notification.helper;

import io.swagger.annotations.ApiModelProperty;

public class SmsConfigMappingDto {
	@ApiModelProperty(notes = "This is SMS Config ID")
    private Long smsConfigId;

	@ApiModelProperty(notes = "Parameter of SMS Config")
    private String parameter;

	@ApiModelProperty(notes = "Parameter Value of SMS Config")
    private String value;

	public Long getSmsConfigId() {
		return smsConfigId;
	}

	public void setSmsConfigId(Long smsConfigId) {
		this.smsConfigId = smsConfigId;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
