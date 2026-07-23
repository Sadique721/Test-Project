package com.savbill.integrationsystem.core.security.spring;//package com.savbill.ticketmanagement.core.security.spring;//package com.savbill.radius.spring;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.DefaultRedirectStrategy;
//import org.springframework.security.web.RedirectStrategy;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//@EnableWebSecurity
//@ComponentScan("com.savbill.radius.security")
//public class SecurityConfig
//{
//
//	@Configuration
//    @Order(2)
//	public static class WebAuthenticator extends WebSecurityConfigurerAdapter{
//
//		@Autowired
//	    private AuthSuccessHandler authHandler;
//
//		@Override
//	    protected void configure(HttpSecurity http) throws Exception {
//	        http
//	            .authorizeRequests()
//	               	.antMatchers(
//	                        "/v2/api-docs",
//	                        "/swagger-resources/**",
//	                        "/swagger-ui.html",
//	                        "/webjars/**"
//	                        ).permitAll()
//	                .anyRequest().authenticated()
//	                .and()
//	            .formLogin()
//	                .loginPage("/login")
//	                .loginProcessingUrl("/authenticateStaff")
//	                .successHandler(authHandler)
//	                .permitAll()
//	                .and()
//	                .logout().permitAll().logoutUrl("/logout")
//	                .and()
//	                .csrf().disable()
//	                ;
//	    }
//
//
//		@Override
//	    public void configure(WebSecurity web) throws Exception {
//	        web
//	           .ignoring()
//	           .antMatchers("/resources/**", "/dist/**", "/bower_components/**", "/plugins/**", "/images/**");
//	    }
//	}
//
//	@Component
//	public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//	    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//	    @Override
//	    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//	        redirectStrategy.sendRedirect(request, response, "home");
//	    }
//	}
//
//
//	@Configuration
//    @Order(1)
//	public static class APIAuthenticator extends WebSecurityConfigurerAdapter
//	{
////		@Autowired
////		StaffService staffService;
////
////		@Bean
////		public BCryptPasswordEncoder passwordEncoder() {
////			return new BCryptPasswordEncoder();
////		}
//
////		@Bean
////		public DaoAuthenticationProvider authenticationProvider()
////		{
////			DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////			authProvider.setUserDetailsService(staffService);
////			authProvider.setPasswordEncoder(passwordEncoder());
////			return authProvider;
////		}
//
////		@Override
////		protected void configure(AuthenticationManagerBuilder auth) throws Exception
////		{
////			auth.authenticationProvider(authenticationProvider());
////		}
//
//		@Override
//		protected void configure(HttpSecurity http) throws Exception
//		{
//			http
//			.cors().and().antMatcher("/api/**")
//            //.httpBasic().and()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and().authorizeRequests()
//            .antMatchers(
//                    "/", "/csrf",
//                    "/v2/api-docs",
//                    "/swagger-resources/**",
//                    "/swagger-ui.html",
//                    "/webjars/**"
//                    ).permitAll()
//            //.anyRequest().authenticated()
//			.and()
//			.csrf().disable();
//		}
//	}
//}
