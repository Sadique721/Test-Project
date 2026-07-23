package com.savbill.commonGateway.spring.security;

import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.security.constants.Constants;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtUtil {
    private int refreshExpirationDateInMs;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.refreshExpirationDateInMs}")
    public void setRefreshExpirationDateInMs(int refreshExpirationDateInMs) {
        this.refreshExpirationDateInMs = refreshExpirationDateInMs;
    }

    public String getRefreshToken(HttpServletRequest request) throws JsonProcessingException {
        Authentication auth = getAuthentication(request);

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());
        LoggedInUser user = (LoggedInUser) auth.getPrincipal();
        LoggedInUser loggedInUser = customUserDetailsService.getLoggedInUserRefreshDataWithStaffId(user.getStaffId());

        String subString = new ObjectMapper().writeValueAsString(loggedInUser);
        //Update sign with with new method
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();
        token = Constants.AUTHORIZATION_TOKEN_PREFIX + " " +token;
        return token;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        String token = req.getHeader(Constants.AUTHORIZATION_HEADER_STRING);
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(Constants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());


        if (token != null) {
            String subject = Jwts.parser()
                    .setSigningKey(hmacKey)
                    .parseClaimsJws(token.replace(Constants.AUTHORIZATION_TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (subject != null) {
                LoggedInUser user = null;
                try {
                    user = new ObjectMapper().readValue(subject, LoggedInUser.class);
                } catch (Exception e) {
                    ApplicationLogger.logger.error(e.getMessage(), e);
                }
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }

            return null;
        }

        return null;
    }
}
