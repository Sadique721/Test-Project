package com.savbill.notification.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Password",description = "This is data transfer object for password")
public class PasswordDto {
	@ApiModelProperty(notes = "Name of the user",required=true)
	private String userName;
	@ApiModelProperty(notes = "New password of the user",required=true)
	private String newPassword;
	@ApiModelProperty(notes = "Confirm new password of the user",required=true)
	private String confirmNewPassword;

	@ApiModelProperty(notes = "This is LastModifiedBy",required=true)
	private String lastModifiedBy;

	public String getLastModifiedBy(){return lastModifiedBy;}
	public void setLastModifiedBy(String lastModifiedBy){this.lastModifiedBy=lastModifiedBy;}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
}
