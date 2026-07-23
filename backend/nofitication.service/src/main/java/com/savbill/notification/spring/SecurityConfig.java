/*
 * package com.savbill.notification.spring;
 * 
 * import java.io.IOException;
 * 
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.ComponentScan; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.core.annotation.Order; import
 * org.springframework.http.codec.ServerCodecConfigurer; import
 * org.springframework.security.authentication.AuthenticationManager; import
 * org.springframework.security.authentication.dao.DaoAuthenticationProvider;
 * import
 * org.springframework.security.config.annotation.authentication.builders.
 * AuthenticationManagerBuilder; import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import
 * org.springframework.security.config.annotation.web.builders.WebSecurity;
 * import org.springframework.security.config.annotation.web.configuration.
 * EnableWebSecurity; import
 * org.springframework.security.config.annotation.web.configuration.
 * WebSecurityConfigurerAdapter; import
 * org.springframework.security.config.http.SessionCreationPolicy; import
 * org.springframework.security.core.Authentication; import
 * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import
 * org.springframework.security.web.DefaultRedirectStrategy; import
 * org.springframework.security.web.RedirectStrategy; import
 * org.springframework.security.web.authentication.
 * SimpleUrlAuthenticationSuccessHandler; import
 * org.springframework.security.web.authentication.
 * UsernamePasswordAuthenticationFilter; import
 * org.springframework.stereotype.Component;
 * 
 * import com.savbill.notification.jwt.JwtAuthenticationEntryPoint; import
 * com.savbill.notification.jwt.JwtAuthenticationFilter; import
 * com.savbill.notification.services.LoginService;
 * 
 * @EnableWebSecurity
 * 
 * @ComponentScan("com.savbill.notification.security") public class SecurityConfig
 * {
 * 
 * @Configuration
 * 
 * @Order(2) public static class WebAuthenticator extends
 * WebSecurityConfigurerAdapter {
 * 
 * @Autowired private AuthSuccessHandler authHandler;
 * 
 * @Override protected void configure(HttpSecurity http) throws Exception {
 * http.authorizeRequests() .antMatchers("/v2/api-docs",
 * "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
 * .anyRequest().authenticated().and().formLogin().loginPage("/login")
 * .loginProcessingUrl("/authenticateStaff").successHandler(authHandler).
 * permitAll().and().logout()
 * .permitAll().logoutUrl("/logout").and().csrf().disable(); }
 * 
 * @Override public void configure(WebSecurity web) throws Exception {
 * web.ignoring().antMatchers("/resources/**", "/dist/**",
 * "/bower_components/**", "/plugins/**", "/images/**"); } }
 * 
 * @Component public class AuthSuccessHandler extends
 * SimpleUrlAuthenticationSuccessHandler {
 * 
 * private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
 * 
 * @Override protected void handle(HttpServletRequest request,
 * HttpServletResponse response, Authentication authentication) throws
 * IOException { redirectStrategy.sendRedirect(request, response, "home"); } }
 * 
 * @Configuration
 * 
 * @Order(1) public static class APIAuthenticator extends
 * WebSecurityConfigurerAdapter {
 * 
 * @Bean public ServerCodecConfigurer serverCodecConfigurer() { return
 * ServerCodecConfigurer.create(); }
 * 
 * @Bean public AuthenticationManager customAuthenticationManager() throws
 * Exception { return authenticationManager(); }
 * 
 * @Autowired JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
 * 
 * @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;
 * 
 * @Autowired private LoginService loginService;
 * 
 * @Bean public BCryptPasswordEncoder passwordEncoder() { return new
 * BCryptPasswordEncoder(); }
 * 
 * @Bean public DaoAuthenticationProvider authenticationProvider() {
 * DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
 * authProvider.setUserDetailsService(loginService);
 * authProvider.setPasswordEncoder(passwordEncoder()); return authProvider; }
 * 
 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
 * Exception { auth.authenticationProvider(authenticationProvider()); }
 * 
 * @Override protected void configure(HttpSecurity http) throws Exception {
 * 
 * http.csrf().disable().cors().and().authorizeRequests().antMatchers(
 * "/api/v1/SavbillNotification/login").permitAll()
 * .antMatchers("/actuator/**").permitAll() .antMatchers("/", "/csrf",
 * "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**")
 * .permitAll().anyRequest().authenticated().and().sessionManagement()
 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().
 * exceptionHandling() .authenticationEntryPoint(jwtAuthenticationEntryPoint);
 * 
 * http.addFilterBefore(jwtAuthenticationFilter,
 * UsernamePasswordAuthenticationFilter.class); }
 * 
 * } }
 */