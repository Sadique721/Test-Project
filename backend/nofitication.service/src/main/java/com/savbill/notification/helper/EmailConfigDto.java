package com.savbill.notification.helper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfigDto 
{
    @ApiModelProperty(notes = "This is email configuration id",required = true)
    private Long emailConfigId;
	@ApiModelProperty(notes = "This is username",required = true)
    private String userName;
	@ApiModelProperty(notes = "This is password",required = true)
    private String password;
	@ApiModelProperty(notes = "This is smtp authentication value",required = true)
    private boolean smtpAuth;
	@ApiModelProperty(notes = "This is auth type",allowableValues = "StartTLS,SSL",  value = "This field accept value only : StartTLS or SSL",required = true)
    private String authType;
	@ApiModelProperty(notes = "This is host value",required = true)
    private String hostServer;
	@ApiModelProperty(notes = "This is port value",required = true)
    private String port;
    @ApiModelProperty(notes = "This is createdBy",required = true)
    private String createdBy;
    @ApiModelProperty(notes = "This is active",allowableValues = "true,false",  value = "This field accept value only : true or false",required = true)
    private Boolean isActive;
    @ApiModelProperty(notes = "This is delete",allowableValues = "false,true",  value = "This field accept value only : false or true",required = true)
    private Boolean isDelete;
    @ApiModelProperty(notes = "This is service type",required = true)
    private String serviceType;
}
