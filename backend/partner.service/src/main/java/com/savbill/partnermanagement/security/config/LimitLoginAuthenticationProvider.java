package com.savbill.partnermanagement.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component("lim" +
        "itLoginAuthenticationProvider")
public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider{

    @Autowired
    @Override
    @Qualifier("customUserDetailService")
    public void setUserDetailsService(UserDetailsService customUserDetailService) {
        // TODO Auto-generated method stub
        super.setUserDetailsService(customUserDetailService);
        super.setPasswordEncoder(passwordEncoder());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        try {

            Authentication auth = super.authenticate(authentication);
            //if reach here, means login success, else an exception will be thrown
            //reset the user_attempts
            return auth;

        } catch (BadCredentialsException e) {
            //invalid login, update to user_attempts
            throw e;
        }

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
