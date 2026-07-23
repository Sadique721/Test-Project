package com.savbill.commonGateway.security.config;

import com.savbill.commonGateway.exceptions.CustomAccessDeniedHandler;
import com.savbill.commonGateway.spring.security.*;
import com.savbill.commonGateway.spring.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@ComponentScan("com.savbill.commonGateway.security")
public class SecurityConfig {

    @Autowired
    @Qualifier("limitLoginAuthenticationProvider")
    AuthenticationProvider authenticationProvider;

    @Value("${jwt.expiration.seconds}")
    String jwtExpirationSeconds;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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
                    .antMatchers(
                            "/v2/api-docs",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/swagger-ui/index.html",
                            "/webjars/**",
                            "/swagger-ui/**"
                    ).permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/websocket-endpoint/**").permitAll()
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
        @Value("${jwt.expiration.seconds}")
        String jwtExpirationSeconds;

        private final CustomUserDetailsService customUserService;

        public APIAuthenticator(CustomUserDetailsService customUserDetailService) {
            this.customUserService = customUserDetailService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillCommonGateway/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/api/v1/SavbillCommonGateway/login").permitAll()
                    .antMatchers("/api/v1/SavbillCommonGateway/passwordPolicyHistory/GeneratePassword").permitAll()
                    .antMatchers("/api/v1/SavbillCommonGateway/staffuser/changepassword").permitAll()
                    .antMatchers("/api/v1/SavbillCommonGateway/otp/generate").permitAll()
                    .antMatchers("/api/v1/SavbillCommonGateway/otp/validate").permitAll()
                    .antMatchers("/api/v1/SavbillCommonGateway/staff/generateTokenByMvnoId/**").permitAll()
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
                    .antMatchers("/websocket-endpoint/**").permitAll()
                    .antMatchers("/provisionCustomer").permitAll()
                    .antMatchers("/deprovisionCustomer").permitAll()
                    .antMatchers("/updateCustomer").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    .addFilter(new JWTAuthenticationFilter(authenticationManager(),jwtExpirationSeconds));
//                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
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
    }


    @Configuration
    @Order(2)
    public static class SubscriberAuthenticator extends WebSecurityConfigurerAdapter {

        CustomSubscriberDetailsService customSubscriberDetailsService;

        public SubscriberAuthenticator(CustomSubscriberDetailsService customSubscriberDetailsService) {
            this.customSubscriberDetailsService = customSubscriberDetailsService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/cpm/portal/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/api/v1/cpm/portal/subscriber/login/**").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/forgotPassword/**").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/validateForgotPassword/**").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/order/process").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/payment/download/**").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/invoice/download/**").permitAll()
                    .antMatchers("/api/v1/cpm/portal/subscriber/document/download/**/**").permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/websocket-endpoint/**").permitAll()
                    .antMatchers("/provisionCustomer").permitAll()
                    .antMatchers("/deprovisionCustomer").permitAll()
                    .antMatchers("/updateCustomer").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new SubscriberJwtAuthorizationFilter(authenticationManager()));
//                    .addFilter(new SubscriberJwtAuthenticationFilter(authenticationManager()));
//                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        }


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(customSubscriberDetailsService)
                    .passwordEncoder(NoOpPasswordEncoder.getInstance());
        }
    }

    @Bean
    public static AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Configuration
    @Order(3)
    public static class RadiusAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/SavbillRadius/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(4)
    public static class TaskMgmtAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/SavbillTaskMgmt/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(5)
    public static class NotificationAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/SavbillNotification/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(6)
    public static class ForgotPwdAPIAuthenticator extends WebSecurityConfigurerAdapter {

        private final CustomUserDetailsService customUserService;

        public ForgotPwdAPIAuthenticator(CustomUserDetailsService customUserDetailService) {
            this.customUserService = customUserDetailService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/staff/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/staff/getStaffContactByUserName").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new ForgetPwdJWTAuthorizationFilter(authenticationManager()))
                    .addFilter(new ForgetPwdJWTAuthenticationFilter(authenticationManager()))
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserService)
                    .passwordEncoder(pwdEncoder());
        }

        @Bean
        public PasswordEncoder pwdEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Configuration
    @Order(7)
    public static class ActuatorConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/actuator/**")
                    .cors()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/actuator/**").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .csrf()
                    .disable();

        }
    }

    @Configuration
    @Order(8)
    public static class SavbillSalesCrmsBssAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillSalesCrmsBss/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(11)
    public static class SavbillIntegrationSystemAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillIntegrationSystem/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(12)
    public static class SavbillKpimanagementAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/KpiManagement/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(13)
    public static class SavbillSampleAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillSample/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(14)
    public static class SavbillInventoryManagementAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillInventoryManagement/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

        @Configuration
        @Order(15)
        public static class SavbillTicketManagementAuthenticator extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.antMatcher("/api/v1/TicketManagement/**")
                        .cors()
                        .and()
                        .csrf()
                        .disable();
            }
        }

    @Configuration
    @Order(16)
    public static class SavbillCPMBssAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/cpm/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(17)
    public static class SavbillRevenueAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/Revenue/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }


    @Configuration
    @Order(18)
    public static class SavbillPartnerAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/pms/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }


    @Configuration
    @Order(19)
    public static class SavbillNotificationAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/SavbillNotification/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(20)
    public static class SavbillNetConfAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/SavbillNetConfManagement/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }


    /**
     * Security Config for IWF Service Manager
     */
    @Configuration
    @Order(21)
    public static class ServiceManagerAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/service-manager/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }
    /**
     * Security Config for IWF Index Coordination Node
     */
    @Configuration
    @Order(22)
    public static class IndexCoordinationAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/index-coordination-node/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }
    /**
     * Security Config for IWF Search agent
     */
    @Configuration
    @Order(23)
    public static class searchAgentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/search-agent/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    /**
     * Security Config for IWF Indexer agent
     */
    @Configuration
    @Order(24)
    public static class indexerAgentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/indexer-agent/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    /**
     * Security Config for IWF Enrichment
     */
    @Configuration
    @Order(25)
    public static class enrichmentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/enrichment/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }
    /**
     * Security Config for IWF Collection
     */
    @Configuration
    @Order(26)
    public static class CollectionAgentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/collection-agent/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    /**
     * Security Config for IWF Enrichment Lite
     */
    @Configuration
    @Order(27)
    public static class enrichmentLiteAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/enrichment-lite/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }


    @Configuration
    @Order(28)
    public static class DiameterAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/SavbillDiameter/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    /**
     * Security Config for IWF log search agent
     */
    @Configuration
    @Order(29)
    public static class logSearchAgentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/log-search-agent/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    /**
     * Security Config for IWF log writer agent
     */
    @Configuration
    @Order(30)
    public static class logWriterAgentAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/log-writer-agent/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }


    @Configuration
    @Order(31)
    public static class SavbillTaskManagementAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/TaskManagement/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(32)  // Any unused order number between existing ones
    public static class OCSAuthenticator extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/ocs/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable();
        }
    }

}
