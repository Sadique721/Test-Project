package com.savbill.notification.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SMS",description = "This is data transfer object for SMS which is used to create new sms")
public class SmsDto 
{
	@ApiModelProperty(notes = "This is Source name of service",required = true)
	private String sourceName;
	@ApiModelProperty(notes = "This is Country Code for sms",required = false)
	private String countryCode;
	@ApiModelProperty(notes = "This is Mobile no for sms",required = true)
	private String mobileNo;
	@ApiModelProperty(notes = "This is message for sms",required = true)
	private String message;
	@ApiModelProperty(notes = "This is status of sms",required = false)
	private String status;
	@ApiModelProperty(notes = "This is SMS Event Id",required = true)
	private Long eventId;

	@ApiModelProperty(notes = "This is createdBy",required = true)
	private String createdBy;

	@ApiModelProperty(notes = "This is service type",value = "This field accept value only : BSS or IWF",required = false)
	private String serviceType;
	@ApiModelProperty(notes = "This is createdBy",required = true)
	private String eventname;
}
