package com.savbill.notification.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Update Email",description = "This is data transfer object for email which is used to update email data")
public class UpdateEmailDto 
{
	@ApiModelProperty(notes = "This is unique email id",required = true)
	private Long emailId;
	@ApiModelProperty(notes = "This is Source name of service",required = true)
	private String sourceName;
	@ApiModelProperty(notes = "This is Email Address",required = true)
	private String emailAddress;
	@ApiModelProperty(notes = "This is message for email",required = true)
	private String message;
	@ApiModelProperty(notes = "This is the status for email",required = false)
	private String status;
	@ApiModelProperty(notes = "This is notification event id",required = true)
	private Long eventId;

	@ApiModelProperty(notes = "This is LastModifiedBy",required = true)
	private String lastModifiedBy;
}
