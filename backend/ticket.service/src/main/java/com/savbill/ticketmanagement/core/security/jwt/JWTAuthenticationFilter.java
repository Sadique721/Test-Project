package com.savbill.ticketmanagement.core.security.jwt;


import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;

import com.savbill.ticketmanagement.core.security.constants.Constants;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
//        this.setFilterProcessesUrl("/api/v1/login");
        this.setFilterProcessesUrl("/swagger-ui.html");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,

                                                HttpServletResponse resp) throws AuthenticationException {
        try {

            StaffUser user = new ObjectMapper().readValue(req.getInputStream(), StaffUser.class);

            return authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException ex) {
//            throw new RuntimeException(ex);
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse resp,
                                            FilterChain filter,
                                            Authentication auth) throws IOException, ServletException {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(Constants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());

        LoggedInUser user = (LoggedInUser) auth.getPrincipal();
        String subString = new ObjectMapper().writeValueAsString(user);
        //Update sign with with new method
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();

        resp.addHeader(Constants.AUTHORIZATION_HEADER_STRING, Constants.AUTHORIZATION_TOKEN_PREFIX + token);
        resp.getWriter().println("{\"status\": 200," +
                " \"message\": \"Login Success\"," +
                " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"," +
                " \"userId\": " + user.getUserId() + "," +
                " \"mvnoId\": " + user.getMvnoId() + "," +
                " \"userRoles\": \"" + user.getRolesList() + "\"," +
                " \"partnerId\": " + user.getPartnerId() + "," +
                " \"serviceAreaId\": " + user.getServiceAreaId() + "," +
                " \"serviceAreaIdList\": " + " \"" + user.getServiceAreaIdList() + "\"," +
                " \"fullName\": " + " \"" + user.getFullName() + "\"," +
                " \"partnerFlag\": " + (Constants.DEFAULT_PARTNER_ID != user.getPartnerId()) + "," +
                " \"isLco\": " + user.getLco() + "," +
                " \"userName\": " + " \"" + user.getUsername() + "\"," +
                " \"teams\": " + " \"" + user.getTeams() + "\"," +
                " \"assignableRoleIds\": " + " \"" + user.getAssignableRoleIds() + "\"," +
                " \"assignableRoleNames\": " + " \"" + user.getAssignableRoleNames() + "\"," +
                " \"accessToken\": \"" + Constants.AUTHORIZATION_TOKEN_PREFIX + token + "\"}");
        //Add log of auth token

//        try {
//            AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);
//            auditLogService.addAuditLogin(AclConstants.ACL_CLASS_STAFF_USER,
//                    AclConstants.OPERATION_LOGIN, req.getRemoteAddr(), null, Long.valueOf(user.getUserId()), user.getUsername(), user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        if(failed.getMessage().equalsIgnoreCase("Bad credentials")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Password is wrong\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        }else if (!failed.getMessage().equalsIgnoreCase("Bad credentials")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Username not found\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        }else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Login Failed\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
        }
    }
}


