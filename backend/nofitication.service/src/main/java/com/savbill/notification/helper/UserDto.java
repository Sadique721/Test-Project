package com.savbill.notification.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class UserDto 
{

	private String username;
	
	private String password;
	
	private Long roleid;

	private String role;
	
	private List<GrantedAuthority> authorities= new ArrayList<>();
	
	public UserDto()
	{
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRoleid() {
		return roleid;
	}

	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}
	
	public List<GrantedAuthority> getAuthorities() 
	{
		return authorities;
	}

	/** MODIFIED SETTER METHOD============================================================================================= **/ 
	
	public void setAuthorities(String role) 
	{
	   GrantedAuthority grantedAuthority= new GrantedAuthority()
	   {

		@Override
		  public String getAuthority() 
		  {
		    return role;
		  }
	   };
	   
	   this.authorities.add(grantedAuthority);
	}

	@Override
	public String toString() 
	{
		return "UserDto [username=" + username + ", password=" + password + ", roleid=" + roleid + ", role=" + role
				+ ", authorities=" + authorities + "]";
	}
	
}
