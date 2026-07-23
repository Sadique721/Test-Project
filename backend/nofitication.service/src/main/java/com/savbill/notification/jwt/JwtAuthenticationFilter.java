package com.savbill.notification.jwt;

import com.savbill.notification.config.CustomHttpServletRequestFilter;
import com.savbill.notification.helper.JwtUserDetail;
import com.savbill.notification.services.LoginService;
import com.savbill.notification.singleton.LoggedInUserSingleton;
import com.savbill.notification.utils.TokenDataExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PropertySource(value = {"classpath:application.properties"})
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String SWAGGER = "swagger";
    public static final String REQUEST_FROM = "requestFrom";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    //final Logger log = Logger.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    LoginService loginService;

    @Autowired
    LoggedInUserSingleton loggedInUserSingleton;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Get Header
        // 2. Start with Bearer or not
        // 3. Validate
//	log.info("***** API called from PORT NO : " + request.getLocalPort() + "*****");

        // String uri = request.getRequestURI();
        // String url = request.getRequestURL().toString();
        CustomHttpServletRequestFilter request = new CustomHttpServletRequestFilter(req);
        String userName = null;
        Long mvnoId = null;
        Long userId = null;
        List<String> teams = null;
        String jWtToken = extractJwtFromRequest(request);
        if (StringUtils.hasText(jWtToken)) {
            try {
                JwtUserDetail userDetail = new ObjectMapper().readValue(this.jwtUtil.extractUsername(jWtToken), JwtUserDetail.class);
                userName = userDetail.getUserName();
                if (userDetail.getId() != 1 && request.getParameter("mvnoId") != null && userDetail.getId() != Long.parseLong(request.getParameter("mvnoId"))) {
                    logger.error("Payload MvnoId does not match with token MvnoId");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Access");
                    return;
                }
                request.addHeader(REQUEST_FROM, SWAGGER);
                // Code for Security // Validate token
                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    MDC.remove("userName");
                    MDC.put("userName", userName);
                    UserDetails userDetails = loginService.loadUserByUsername(userName);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
//                    logger.error("Token is not valid");
                    request.setAttribute("inValid", "Token is not valid");
                }
            } catch (IllegalArgumentException e) {
                MDC.remove("userName");
                logger.error("An error occured during getting username from token", e);
                request.setAttribute("inValid", "Token is not valid. An error occured during getting username from token");
            } catch (ExpiredJwtException e) {
                MDC.remove("userName");
                String isRefreshToken = request.getHeader("isRefreshToken");
                String requestURL = request.getRequestURL().toString();
                // allow for Refresh Token creation if following conditions are true.
                if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshToken")) {
                    allowForRefreshToken(e, request);
                } else {
//                    logger.warn("Token is expired and not valid anymore", e);
                    request.setAttribute("expired", "Token is expired and not valid anymore");
                }
            } catch (SignatureException e) {
                MDC.remove("userName");
//                logger.warn("Token is not valid", e);
                request.setAttribute("inValid", "Token is not valid");
            } catch (Exception e) {
                MDC.remove("userName");
                // throw new RuntimeException(e.getMessage());
//                logger.error("Token is not valid");
                request.setAttribute("inValid", "Token is not valid");
            }
        } else {
//            logger.warn("Couldn't find bearer string, will ignore the header");
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
        if(request.getHeader("Authorization") != null){
            String decodedToken = tokenDataExtractor.getDecoded(request.getHeader("Authorization"));
            if (decodedToken != null) {
                JSONObject primaryObject = new JSONObject(decodedToken);
                JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
                userName = mainObj.getString("username");
                mvnoId= mainObj.getLong("mvnoId");
                userId= mainObj.getLong("userId");
                JSONObject subObject = new JSONObject(primaryObject.getString("sub"));
                JSONArray teamsArray = subObject.getJSONArray("teams");
                teams = new ArrayList<>();
                for (int i = 0; i < teamsArray.length(); i++) {
                    String teamId = teamsArray.getString(i);
                    teams.add(teamId);
                }
            }
            loggedInUserSingleton.setUserName(userName);
            loggedInUserSingleton.setMvnoId(mvnoId);
            loggedInUserSingleton.setUserId(userId);
            loggedInUserSingleton.setTeams(teams);
            MDC.put("userName", userName);
            MDC.put("mvnoId", String.valueOf(mvnoId));
            MDC.put("userId", String.valueOf(userId));
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


        //MDC.put("spanId", UUID.randomUUID().toString().replaceAll("-", ""));
        filterChain.doFilter(request, response);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(null, null, null);
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
