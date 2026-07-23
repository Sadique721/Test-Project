package com.savbill.notification.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.savbill.notification.spring.UserDetailsServiceImplementor;

@Configuration

public class SpringSecConfig extends WebSecurityConfigurerAdapter {
  
	

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
	
	httpSecurity.csrf().disable();
	httpSecurity.headers().frameOptions().disable();
    
     httpSecurity.httpBasic().disable();
    
    }

    
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception 
	{
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean 
    public UserDetailsServiceImplementor userDetailsServiceImplementormethod()
    {
    	return new UserDetailsServiceImplementor();
    }
	
	
    @Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsServiceImplementormethod());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
    
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception
    {
	return authenticationManager();
    }
  
}

