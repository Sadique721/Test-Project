package com.savbill.notification.spring;

import com.savbill.notification.helper.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.savbill.notification.utils.Dao;

public class UserDetailsServiceImplementor implements UserDetailsService
{
	@Autowired
	Dao dao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		UserDetails userDetails=null;

    	UserDto userDto =dao.getUserDetailsFromUsername(username); /** we get username,password,roleid From this **/
              if(userDto!=null)
              {
            	 String role=dao.getRoleFromRoleId(userDto.getRoleid());
            	 if(role!=null)
            	 {
            		 userDto.setRole(role);
            		 userDto.setAuthorities(role);
            	 }

            	 userDetails= new org.springframework. security.core.userdetails.User(userDto.getUsername(),userDto.getPassword(),userDto.getAuthorities());
              }
    	return userDetails;
	}

}
