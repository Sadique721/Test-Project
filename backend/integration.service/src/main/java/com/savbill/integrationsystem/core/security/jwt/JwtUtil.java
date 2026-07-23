package com.savbill.integrationsystem.core.security.jwt;//package com.savbill.ticketmanagement.core.security.jwt;


import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JwtUtil {
    @Value(value = "${isprolename}")
    private String isprolename;
    @Value(value = "${superadmin.username}")
    private String username;
    @Value(value = "${superadmin.password}")
    private String password;
    @Value(value = "${superadmin.mvnoname}")
    private String mvnoname;

    @Value(value = "${admin.username}")
    private String adminUsername;
    @Value(value = "${admin.password}")
    private String adminPassword;
    @Value(value = "${admin.mvnoname}")
    private String adminMvnoname;



    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {

            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }

        } catch (Exception e) {
            user = null;
        }
        return user;
    }
    public String generateJwtToken(Long mvnoId) {

        List<GrantedAuthority> role_name = new ArrayList<>();
        if (mvnoId==1) {
            role_name.add(new SimpleGrantedAuthority(isprolename));
        }else {
            role_name.add(new SimpleGrantedAuthority("ADMIN"));
        }
        LoggedInUser user = this.loggedInUser(mvnoId);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, role_name);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return GenerateTokenUsingLoggedInUser(user);
    }

    public LoggedInUser loggedInUser(Long mvnoId){
        LoggedInUser user = null;
        if (mvnoId==1) {
             user = new LoggedInUser(
                    username,
                    "superadmin",
                    "superadmin",
                    1,
                    1,
                    "1",
                    null,
                    1,
                    new ArrayList<>(),
                    1,
                    new ArrayList<Long>(),
                    new ArrayList<Long>(),
                    new ArrayList<Long>(),
                     mvnoname,
                    new ArrayList<>(),
                    null,
                    null,
                    false,
                    password,
                    true,
                    true,
                    true,
                    true,
                    new ArrayList<>(),
                    LocalDateTime.now()
            );
        }else {
             user = new LoggedInUser(
                     adminUsername,
                    "admin",
                    "admin",
                    2,
                    1,
                    "1",
                    null,
                    2,
                     new ArrayList<>(),
                    2,
                    new ArrayList<Long>(),
                    Arrays.asList(1L),
                    Arrays.asList(1L, 11L, 10L),
                     adminMvnoname,
                     new ArrayList<>(),
                    null,
                    null,
                    false,
                    adminPassword,
                    true,
                    true,
                    true,
                    true,
                    new ArrayList<>(),
                    LocalDateTime.now()
             );
        }

        return  user;
    }

    public String GenerateTokenUsingLoggedInUser(LoggedInUser loggedInUser){

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode("asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"),
                SignatureAlgorithm.HS256.getJcaName());
        String subString = null;
        try {
            subString = new ObjectMapper().writeValueAsString(loggedInUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        long expirationTime = 1732354051L * 1000L; // Convert seconds to milliseconds

        //Update sign with with new method
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + CommonConstant.EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();
        token =  "Bearer " + " " +token;
        return token;
    }

}
