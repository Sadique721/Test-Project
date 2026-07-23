package com.savbill.notification.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Login",description = "This is data transfer object for login")
public class LoginDto 
{
	@ApiModelProperty(notes = "Name of the user",required=true)
	private String userName;
	@ApiModelProperty(notes = "Password of the user",required=true)
	private String password;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LoginDto [userName=" + userName + ", password=" + password + "]";
	}
	
}
