package com.savbill.radius.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.savbill.radius.helper.LoginDto;

public interface LoginService extends UserDetailsService{

    String login(LoginDto loginData);
    
}
