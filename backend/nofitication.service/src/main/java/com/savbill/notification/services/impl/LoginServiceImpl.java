package com.savbill.notification.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.savbill.notification.helper.UserDto;
import com.savbill.notification.jwt.JwtUtil;
//import com.savbill.notification.rabbitmq.MessageReceiver;
import com.savbill.notification.repository.StaffRepository;
import com.savbill.notification.services.LoginService;
import com.savbill.notification.utils.Dao;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String BAD_CREDENTIALS = "Bad Credentials";

    

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    MessageReceiver messageReceiver;
    
    @Autowired
    StaffRepository staffRepository;

    public static Long mvno=null;
    
    @Autowired
    Dao dao;
    
    
 // This method is used TO ================================================================== authenticate user.
    @Override
    public UserDetails loadUserByUsername(String userName)
    {

    	UserDetails userDetails=null;

    	UserDto userDto =dao.getUserDetailsFromUsername(userName); /** we get username,password,roleid From this **/
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
  

