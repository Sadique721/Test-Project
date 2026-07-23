/*
 package com.savbill.radius.spring;





import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@ComponentScan("com.savbill.radius.security")
public class SecurityConfig 
{
		
	@Configuration
    @Order(2)	
	public static class WebAuthenticator extends WebSecurityConfigurerAdapter{

		@Autowired
	    private AuthSuccessHandler authHandler;
		
		@Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http
	            .authorizeRequests()      
	               	.antMatchers(                 
	                        "/v2/api-docs", 
	                        "/swagger-resources/**",
	                        "/swagger-ui.html",
	                        "/**"
	                        ).permitAll()
	                .anyRequest().authenticated()
	                .and()
	            .formLogin()
	                .loginPage("/login")
	                .loginProcessingUrl("/authenticateStaff")
	                .successHandler(authHandler)
	                .permitAll()                   
	                .and()
	                .logout().permitAll().logoutUrl("/logout")
	                .and()
	                .csrf().disable()
	                ;
	    }
	}
	
	@Component
	public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	    @Override
	    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
	        redirectStrategy.sendRedirect(request, response, "home");
	    }
	}
	
}

*/
