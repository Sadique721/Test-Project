package com.savbill.notification.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Email",description = "This is data transfer object for Email which is used to create new email")
public class
EmailDto
{
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

	@ApiModelProperty(notes = "This is CreatedBy",required = true)
	private String createdBy;

	@JsonProperty("date")
	@JsonFormat(pattern="yyyy-MM-dd, HH:mm:ss")
	private LocalDateTime date;

	@ApiModelProperty(notes = "This is the status for email",required = false)
	private String emailSubject;

}
