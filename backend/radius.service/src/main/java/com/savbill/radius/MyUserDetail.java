package com.savbill.radius;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.savbill.radius.entity.Staff;

public class MyUserDetail implements UserDetails 
{
	private transient Staff user;
	
	public MyUserDetail(Staff user) {
		super();
		this.user = user;
	}

	public MyUserDetail() {
		super();
	}


	@Override
	public String getPassword() 
	{
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.user.getRole().getName());
		authorities.add(authority);
		return authorities;
	    //return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
}
