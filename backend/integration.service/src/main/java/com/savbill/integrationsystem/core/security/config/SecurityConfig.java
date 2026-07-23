package com.savbill.integrationsystem.core.security.config;

import com.savbill.integrationsystem.core.security.jwt.JWTAuthenticationFilter;
import com.savbill.integrationsystem.core.security.jwt.JWTAuthorizationFilter;
import com.savbill.integrationsystem.core.security.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@ComponentScan("com.savbill.integrationsystem.core.security.config")
public class SecurityConfig {
    @Autowired
    @Qualifier("limitLoginAuthenticationProvider")
    AuthenticationProvider authenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    public static class WebAuthenticator extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthSuccessHandler authHandler;

        //"/login","/dist/**","/bower_components/**","/plugins/**","/authenticateStaff","/customers/changepassword"

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/customers/changepassword").permitAll()
                    .antMatchers("/customers/updatepassword").permitAll()
                    .antMatchers("/Customer").permitAll()
                    .antMatchers("/provisionCustomer").permitAll()
                    .antMatchers("/deprovisionCustomer").permitAll()
                    .antMatchers("/getCustomer").permitAll()
                    .antMatchers("/updateCustomer").permitAll()
                    .antMatchers("/ws/**").permitAll()
                    .antMatchers("/new-api/QodServices/**").permitAll()
                    .antMatchers("/NPM_API_11.1.1.5/services/ServiceIfcPort/**").permitAll()
                    .antMatchers("/restApi/**").permitAll()
                    //.antMatchers("/api/v1/SavbillIntegrationSystem/saveMvno").permitAll()
                    .antMatchers("/invoiceIntigration/**").permitAll()
                    //.antMatchers("/api/v1/SavbillIntegrationSystem/recordPayment").permitAll()
                    .antMatchers(
                            "/v2/api-docs",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/webjars/**"
                    ).permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/callback").permitAll()
                    .antMatchers("/momoCallback").permitAll()
                    .antMatchers("/paystack/callback").permitAll()
                    .antMatchers(("/callback")).permitAll()
                    .antMatchers("/selcomWebHook").permitAll()
                    .antMatchers("/FetchPaymentReceipt").permitAll()
                    .antMatchers("/waveMoneyCallBack").permitAll()
                    .antMatchers("/kbzPayCallBack").permitAll()
                    .antMatchers("/onePayCallBack").permitAll()
                    .antMatchers("/transacteaseCallback").permitAll()
                    .antMatchers("/airtelWebHook").permitAll()
                    .antMatchers("/b2c/result").permitAll()
                    .antMatchers("/b2c/queue").permitAll()
                    .antMatchers("/ussd/**").permitAll()
                    .antMatchers("/selcomAppToCRM/validation").permitAll()
                    .antMatchers("/selcomAppToCRM/notification").permitAll()
                    .antMatchers("/callLogs/save").permitAll()
                    .antMatchers("/validateC2BRequest").permitAll()
                    .antMatchers("/c2b/confirmation").permitAll()
                    .antMatchers("/mpesa/expressSimulate/callback").permitAll()
                    .antMatchers("/mpesa/c2b/payment").permitAll()
                    .antMatchers(
                            "/api/v1/SavbillIntegrationSystem/crdb/**"
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


        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/dist/**", "/bower_components/**", "/plugins/**", "/images/**", "/payment-response");
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


    @Configuration
    @Order(1)
    public static class APIAuthenticator extends WebSecurityConfigurerAdapter {

        private final CustomUserDetailsService customUserService;

        public APIAuthenticator(CustomUserDetailsService customUserDetailService) {
            this.customUserService = customUserDetailService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers(
                            "/v2/api-docs",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/webjars/**"
                    ).permitAll()
                    .antMatchers("/api/v1/login").permitAll()
                    .antMatchers("/api/v1/generatePaytmLinkAndSendToCustomer").permitAll()
                    .antMatchers("/api/v1/payment/**").permitAll()
                    .antMatchers("/api/v1/subscriber/payment/download/**").permitAll()
                    .antMatchers("/api/v1/subscriber/invoice/download/**").permitAll()
                    .antMatchers("/api/v1/subscriber/document/download/**/**").permitAll()
                    .antMatchers("/api/v1/order/process").permitAll()
                    .antMatchers("/api/v1/subscriber/forgotPassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/validateForgotPassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/customer/updatePassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/staff/updatePassword/**").permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/ussd/**").permitAll()
                    .antMatchers("/ws/**").permitAll()
                    .antMatchers("/new-api/QodServices/**").permitAll()
                    .antMatchers("/NPM_API_11.1.1.5/services/ServiceIfcPort/**").permitAll()
                    .antMatchers("/restApi/**").permitAll()
                    .antMatchers("/invoiceIntigration/**").permitAll()
                    .antMatchers("/api/v1/SavbillIntegrationSystem/saveMvno").permitAll()
                    .antMatchers("/api/v1/SavbillIntegrationSystem/recordPayment").permitAll()
                    .antMatchers("/api/v1/SavbillIntegrationSystem/mpesa/c2b/payment").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    .addFilter(new JWTAuthenticationFilter(authenticationManager()));
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserService)
                    .passwordEncoder(encoder());
        }

        @Bean
        public PasswordEncoder encoder() {
            return new BCryptPasswordEncoder();
        }
        @Configuration
        @Order(8)
        public static class ActuatorConfiguration extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.antMatcher("/actuator/**")
                        .cors()
                        .and()
                        .authorizeRequests()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/api/v1/SavbillIntegrationSystem/saveMvno").permitAll()
                        .antMatchers("/api/v1/SavbillIntegrationSystem/recordPayment").permitAll()
                        .anyRequest()
                        .authenticated()
                        .and()
                        .csrf()
                        .disable();

            }
        }
    }
}
