package com.savbill.commonGateway.spring.security;

import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.exceptions.AccountLockedException;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.LoginAttemptService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.SpringContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    // Use ConcurrentHashMap to track login attempts
    private final ConcurrentHashMap<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final AuthenticationManager authenticationManager;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    private final String jwtExpirationSeconds;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String jwtExpirationSeconds) {
        this.authenticationManager = authenticationManager;
        this.jwtExpirationSeconds = jwtExpirationSeconds;
        this.setFilterProcessesUrl("/api/v1/SavbillCommonGateway/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        try {
            StaffUser user = new ObjectMapper().readValue(req.getInputStream(), StaffUser.class);
            req.setAttribute("username", user.getUsername());
            req.setAttribute("staffUserRepository", staffUserRepository);
            long startTime = System.nanoTime();

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>()));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            // remove SOP print
            // LOGGER.info("Elapsed Time:::::::: attemptAuthentication" + elapsedTime + " nanoseconds");
            return authentication;
        } catch (IOException ex) {
//            throw new RuntimeException(ex);
            // also remove printstacktrace
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain filter, Authentication auth) throws IOException, ServletException {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET), SignatureAlgorithm.HS256.getJcaName());
        LoggedInUser user = (LoggedInUser) auth.getPrincipal();
        String subString = new ObjectMapper().writeValueAsString(user);
        //Update sign with  new method
        String token = Jwts.builder().setSubject(subString).setExpiration(new Date(System.currentTimeMillis() + (Long.parseLong(jwtExpirationSeconds) * 1000))).signWith(hmacKey).compact();
        resp.addHeader(CommonConstants.AUTHORIZATION_HEADER_STRING, CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token);
        resp.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        resp.getWriter().println("{\"status\": 200," + " \"message\": \"Login Success\"," + " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"," + " \"userId\": " + user.getUserId() + "," + " \"mvnoId\": " + user.getMvnoId() + "," + " \"userRoles\": \"" + user.getRolesList() + "\"," + " \"partnerId\": " + user.getPartnerId() + "," + " \"serviceAreaId\": " + user.getServiceAreaId() + "," + " \"serviceAreaIdList\": " + " \"" + user.getServiceAreaIdList() + "\"," + " \"fullName\": " + " \"" + user.getFullName() + "\"," + " \"partnerFlag\": " + (CommonConstants.DEFAULT_PARTNER_ID != user.getPartnerId()) + "," + " \"isLco\": " + user.getLco() + "," + " \"userName\": " + " \"" + user.getUsername() + "\"," + " \"mvnoName\": " + " \"" + user.getMvnoName() + "\"," + " \"teams\": " + " \"" + user.getTeams() + "\"," + " \"teamIds\": " + " \"" + user.getTeamIds() + "\"," + " \"assignableRoleIds\": " + " \"" + user.getAssignableRoleIds() + "\"," + " \"assignableRoleNames\": " + " \"" + user.getAssignableRoleNames() + "\"," + " \"accessToken\": \"" + CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token + "\"}");
        //Add log of auth token

//        try {
////            AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);
////            auditLogService.addAuditLogin(AclConstants.ACL_CLASS_STAFF_USER,
////                    AclConstants.OPERATION_LOGIN, req.getRemoteAddr(), null, Long.valueOf(user.getUserId()), user.getUsername(), user);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        String username = (String) request.getAttribute("username");
        LoginAttemptService loginAttemptService = SpringContext.getBean(LoginAttemptService.class);

        if (username != null) {
            try {
                loginAttemptService.handleLoginAttempt(username, response);
            } catch (AccountLockedException e) {
                // Handle the account lock exception
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("{\"status\": 401," + " \"message\": \"" + e.getMessage() + "\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
                MDC.clear();
                return;
            } catch (UsernameNotFoundException Ue) {
                // Handle the UsernameNotFoundException
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("{\"status\": 401," + " \"message\": \"" + Ue.getMessage() + "\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
                MDC.clear();
                return;
            }
        }

        // Handle Bad Credentials
        if (failed.getMessage().equalsIgnoreCase("Bad credentials")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," + " \"message\": \"Wrong Password , Try using Different Password\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        } else if (failed.getMessage().equalsIgnoreCase("UserDetailsService returned null, which is an interface contract violation")) {
            // Handle Expired Password
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.getWriter().println("{\"status\": 406," + " \"message\": \"Password is expired, please reset\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        } else if (!failed.getMessage().equalsIgnoreCase("Bad credentials")) {
            // Handle Username Not Found
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," + " \"message\": \"Username not found\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        } else {
            // Generic Login Failure Message
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," + " \"message\": \"Login failed\"," + " \"timestamp\": \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        }
    }
}
