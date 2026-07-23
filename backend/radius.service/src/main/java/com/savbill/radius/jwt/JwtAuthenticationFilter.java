package com.savbill.radius.jwt;

import com.savbill.radius.aaa.config.CustomHttpServletRequestFilter;
import com.savbill.radius.helper.TokenDetail;
import com.savbill.radius.services.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@PropertySource(value = {"classpath:application.properties"})
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    public static final String SWAGGER = "swagger";
    public static final String REQUEST_FROM = "requestFrom";
    private static final String MVNO_ID_FROM_APIGW = "mvnoIdFromApigw";
    private static final String STAFF_ID_FROM_APIGW = "staffIdFromApigw";
    private static final String SERVICE_AREA_IDS = "serviceAreaIdList";

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Get Header
        // 2. Start with Bearer or not
        // 3. Validate
//	log.info("***** API called from PORT NO : " + request.getLocalPort() + "*****");

        // String uri = request.getRequestURI();
        // String url = request.getRequestURL().toString();
        CustomHttpServletRequestFilter request = new CustomHttpServletRequestFilter(req);
        String userName = null;
        TokenDetail readValue = null;
//	String requestFrom = request.getHeader("requestFrom");
        String jWtToken = extractJwtFromRequest(request);
        if (StringUtils.hasText(jWtToken)) {
            try {
                userName = this.jwtUtil.extractUsername(jWtToken);
                readValue = new ObjectMapper().readValue(userName, TokenDetail.class);
                request.addHeader(REQUEST_FROM, SWAGGER);
                // Code for Security // Validate token
                if (userName != null && readValue != null && readValue.getFirstName() != null) {
                    MDC.remove("userName");
                    MDC.put("userName", userName);
                    UserDetails userDetails = loginService.loadUserByUsername(readValue.getFirstName());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    logger.error("Token is not valid");
                    request.setAttribute("inValid", "Token is not valid");
                }
            } catch (IllegalArgumentException e) {
                MDC.remove("userName");
                logger.error("An error occured during getting username from token", e);
                request.setAttribute("inValid",
                        "Token is not valid. An error occured during getting username from token");
            } catch (ExpiredJwtException e) {
                MDC.remove("userName");
                String isRefreshToken = request.getHeader("isRefreshToken");
                String requestURL = request.getRequestURL().toString();
                // allow for Refresh Token creation if following conditions are true.
                if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshToken")) {
                    allowForRefreshToken(e, request);
                } else {
                    logger.warn("Token is expired and not valid anymore", e);
                    request.setAttribute("expired", "Token is expired and not valid anymore");
                }
            } catch (SignatureException e) {
                MDC.remove("userName");
                logger.warn("Token is not valid", e);
                request.setAttribute("inValid", "Token is not valid");
            } catch (Exception e) {
                // throw new RuntimeException(e.getMessage());
                MDC.remove("userName");
                logger.error("Token is not valid");
                request.setAttribute("inValid", "Token is not valid");
            }
        } else {
            logger.info("Couldn't find bearer string, will ignore the header");
            request.setAttribute("inValid", "Token is not valid. Couldn't find bearer string");
        }
        if (userName == null && request.getHeaders("userName").hasMoreElements()) {
            MDC.remove("userName");
            MDC.put("userName", request.getHeaders("userName").nextElement());
        }
        if (request.getHeaders("traceId").hasMoreElements()) {
            MDC.remove("traceId");
            MDC.put("traceId", request.getHeaders("traceId").nextElement());
        } else {
            MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
        }
        if (readValue == null) {
            if ((request.getHeaders(STAFF_ID_FROM_APIGW).hasMoreElements())) {
                MDC.put(STAFF_ID_FROM_APIGW, request.getHeaders(STAFF_ID_FROM_APIGW).nextElement());
            }
            if ((request.getHeaders(MVNO_ID_FROM_APIGW).hasMoreElements())) {
                MDC.put(MVNO_ID_FROM_APIGW, request.getHeaders(MVNO_ID_FROM_APIGW).nextElement());
            }
        } else {
            MDC.put(STAFF_ID_FROM_APIGW, String.valueOf(readValue.getStaffId()));
            MDC.put(MVNO_ID_FROM_APIGW, String.valueOf(readValue.getMvnoId()));
        }
        MDC.put("spanId", UUID.randomUUID().toString().replaceAll("-", ""));

        filterChain.doFilter(request, response);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    null, null, null);
            // After setting the Authentication in the context, we specify that the current
            // user is authenticated. So it passes the
            // Spring Security Configurations successfully.
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            // Set the claims so that in controller we will be using it to create new JWT
            // request.setAttribute("claims", ex.getClaims());
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader(AUTHORIZATION);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
                return bearerToken.substring(7, bearerToken.length());
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
