package com.savbill.integrationsystem.core.security.jwt;


import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;

import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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
import java.util.UUID;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String MVNO_ID_FROM_APIGW = "mvnoIdFromApigw";
    private static final String STAFF_ID_FROM_APIGW = "staffIdFromApigw";

    private static final String STAFF_USERNAME = "userName";

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    private String getTokenFromHeaders(HttpServletRequest request) {
        String authHeader = request.getHeader(Constants.AUTHORIZATION_HEADER_STRING);
        String apiKeyHeader = request.getHeader(Constants.APIKEY_HEADER_STRING);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        } else if (apiKeyHeader != null) {
            return apiKeyHeader;
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String header = getTokenFromHeaders(req);
            if (header == null || !header.startsWith(Constants.AUTHORIZATION_TOKEN_PREFIX)) {
                filterChain.doFilter(req, resp);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String userName=((LoggedInUser)securityContext.getAuthentication().getPrincipal()).getFullName();
            MDC.put("userName", userName);
            if(userName == null && req.getHeaders("userName").hasMoreElements()) {
                MDC.remove("userName");
                MDC.put("userName", req.getHeaders("userName").nextElement());
            }
            if(req.getHeaders("traceId").hasMoreElements()) {
                MDC.remove("traceId");
                MDC.put("traceId", req.getHeaders("traceId").nextElement());
            } else {
                MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
            }
            MDC.put("spanId", UUID.randomUUID().toString().replaceAll("-", ""));
            filterChain.doFilter(req, resp);
        } catch (ExpiredJwtException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("{\"status\": 401," +
                    " \"message\": \"JWT Expired\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        String token = getTokenFromHeaders(req);
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(Constants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());


        if (token != null) {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
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


