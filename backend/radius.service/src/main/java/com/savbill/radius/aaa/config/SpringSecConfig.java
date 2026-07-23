package com.savbill.radius.aaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.savbill.radius.jwt.JwtAuthenticationFilter;

@Configuration
public class SpringSecConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
	httpSecurity.authorizeRequests().antMatchers("/", "/swagger-resources/**").permitAll();
	//httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	httpSecurity.csrf().disable();
	httpSecurity.headers().frameOptions().disable();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
	return authenticationManager();
    }
    
    @Bean
   	public BCryptPasswordEncoder passwordEncoder() {
   		return new BCryptPasswordEncoder();
   	}
}