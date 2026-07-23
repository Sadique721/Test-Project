package com.savbill.notification.helper;

import io.swagger.annotations.ApiModelProperty;

public class NotificationConfigMappingDto {
	@ApiModelProperty(notes = "This is SMS Config ID")
    private Long notificationConfigId;

	@ApiModelProperty(notes = "Parameter of SMS Config")
    private String parameter;

	@ApiModelProperty(notes = "Parameter Value of SMS Config")
    private String value;

	public Long getNotificationConfigId() {
		return notificationConfigId;
	}

	public void setNotificationConfigId(Long notificationConfigId) {
		this.notificationConfigId = notificationConfigId;
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
