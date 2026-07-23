package com.savbill.ticketmanagement.core.security.filter;

import com.savbill.ticketmanagement.core.security.constants.Constants;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;

public class MyFilter extends GenericFilterBean {
    private static final String MVNO_ID_FROM_APIGW = "mvnoIdFromApigw";
    private static final String STAFF_ID_FROM_APIGW = "staffIdFromApigw";

    private static final String STAFF_USERNAME = "userName";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        UsernamePasswordAuthenticationToken data = getAuthentication((HttpServletRequest) request);
        LoggedInUser loggedInUser = ((LoggedInUser) data.getPrincipal());
        RequestContext ctx = RequestContext.getCurrentContext();

        ctx.addZuulRequestHeader(MVNO_ID_FROM_APIGW, loggedInUser.getMvnoId().toString());
        ctx.addZuulRequestHeader(STAFF_ID_FROM_APIGW, loggedInUser.getStaffId().toString());

        request.setAttribute(MVNO_ID_FROM_APIGW, loggedInUser.getMvnoId());
        request.setAttribute(STAFF_ID_FROM_APIGW, loggedInUser.getStaffId());
        request.setAttribute(STAFF_USERNAME, loggedInUser.getUsername());
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        String token = req.getHeader(Constants.AUTHORIZATION_HEADER_STRING);
//		String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJmaXJzdE5hbWVcIjpcImFkbWluXCIsXCJsYXN0TmFtZVwiOlwiYWRtaW5cIixcInVzZXJJZFwiOjIsXCJwYXJ0bmVySWRcIjoxLFwicm9sZXNMaXN0XCI6XCIxXCIsXCJzZXJ2aWNlQXJlYUlkXCI6bnVsbCxcIm12bm9JZFwiOjIsXCJzZXJ2aWNlQXJlYUlkTGlzdFwiOlsxLDIsNCw1LDYsNyw4LDksMTAsMTMsMTQsMTUsMTYsMTcsMTgsMTksMjIsMjMsMjQsMjUsMjcsMjgsMzAsMzEsMzIsMzMsNDcsNDgsNDksNTAsNTEsNTIsNTMsNTQsNTUsNTYsNTcsNTgsNTksNjAsNjEsNjIsNjUsNjYsNjcsNjgsNjksNzYsNzcsNzgsNzksODEsODIsODMsODQsOTBdLFwic3RhZmZJZFwiOjIsXCJidUlkc1wiOltdLFwibGNvXCI6ZmFsc2V9IiwiZXhwIjoxNjc2MDUyNzYzfQ.UdeQCUG6wQPA7tsdJDWie0GpcTgC6H5Mh1KIwiRdeB8";
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
