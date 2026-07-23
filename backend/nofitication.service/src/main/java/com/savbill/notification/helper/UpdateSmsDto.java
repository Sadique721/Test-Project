package com.savbill.notification.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Update SMS",description = "This is data transfer object for SMS which is used to update sms data")
public class UpdateSmsDto 
{
	@ApiModelProperty(notes = "This is unique sms id",required = true)
	private Long smsId;
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

	@ApiModelProperty(notes = "This is LastModifiedBy",required = true)
	private String lastModifiedBy;

	@ApiModelProperty(notes = "This is service type",value = "This field accept value only : BSS or IWF",required = false)
	private String serviceType;
}
