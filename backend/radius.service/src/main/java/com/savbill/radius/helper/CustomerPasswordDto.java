package com.savbill.radius.helper;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Password",description = "This is data transfer object for password which is used to update customer password")
public class CustomerPasswordDto
{
	@ApiModelProperty(notes = "Name of the user",required=true)
	private String userName;
	@ApiModelProperty(notes = "New password of the user",required=true)
	private String newPassword;
	@ApiModelProperty(notes = "Confirm new password of the user",required=true)
	private String confirmNewPassword;
	@ApiModelProperty(hidden = true)
	private Integer mvnoId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}
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
	
	public CustomerPasswordDto() 
	{
		super();
	}
	
	public CustomerPasswordDto(Map<String,Object> map)
	{
		if(map.get("userName") != null)
		{
			this.setUserName(map.get("userName").toString());
		}
		if(map.get("newPassword") != null)
		{
			this.setNewPassword(map.get("newPassword").toString());
		}
		if(map.get("confirmNewPassword") != null)
		{
			this.setConfirmNewPassword(map.get("confirmNewPassword").toString());
		}
		if(map.get("mvnoId") != null)
		{
			this.mvnoId = Integer.parseInt(map.get("mvnoId").toString());
		}
	}
}
