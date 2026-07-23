package com.savbill.commonGateway.spring.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomHeaderFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "howtotrainyourdragon";

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
//        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpServletResponse);
//
//        String receivedHmac = wrappedRequest.getHeader("X-HMAC-SIGNATURE");
//        String reqMilliSec = wrappedRequest.getHeader("X-REQUEST-MILLISEC");
//        String requestBody = extractBody(wrappedRequest);
//
//        if (requestBody != null && !requestBody.isEmpty()) {
//            String calculatedHmac = calculateHmac(requestBody + reqMilliSec, SECRET_KEY);
//
//            if (receivedHmac == null || !receivedHmac.equals(calculatedHmac)) {
//                HttpServletResponse httpResponse = (HttpServletResponse) response;
//                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                httpResponse.getWriter().write("{\"error\":\"Invalid request HMAC\"}");
//                httpResponse.setContentType("application/json");
//                return;
//            }
//        }
//
//
//        // Proceed with wrapped response
//        chain.doFilter(wrappedRequest, wrappedResponse);
//        // Get response body
//        byte[] contentAsBytes = wrappedResponse.getContentAsByteArray();
//        String responseBody = new String(contentAsBytes, response.getCharacterEncoding());
//        long currentMillis = System.currentTimeMillis();
//
//        responseBody = responseBody + currentMillis;
//        // :white_check_mark: Log or inspect the body
//        System.out.println("Response Body: " + responseBody);
//        String hmac = calculateHmac(responseBody, SECRET_KEY);
//        System.out.println("Response HMAC ::::::::::::: " + hmac);
//        // :white_check_mark: Add custom header
//        wrappedResponse.setHeader("X-RESPONSE-HMAC", hmac);
//        wrappedResponse.setHeader("X-currentMillis", String.valueOf(currentMillis));
//        // Write the buffered content to actual response
//        wrappedResponse.copyBodyToResponse();
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Wrap request and response
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String skipHash = request.getHeader("X-Skip-Hash");

        // ✅ Extract request headers
        String receivedHmac = wrappedRequest.getHeader("X-HMAC-SIGNATURE");
        String reqMilliSec = wrappedRequest.getHeader("X-REQUEST-MILLISEC");
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
        String rawQuery = request.getQueryString();
        String queryString;
        try {
            if(rawQuery != null){
                if("POST".equalsIgnoreCase(request.getMethod())) {
                    queryString = rawQuery;
                }else{
                    queryString = URLDecoder.decode(rawQuery, "UTF-8");
                }
            } else {
                queryString = rawQuery;
            }
        } catch (Exception e) {
            queryString = rawQuery;
        }
        // ✅ Extract request body after chain
        String requestBody = new String(requestBodyBytes , wrappedRequest.getCharacterEncoding());
        String calculatedHmac = null;
        String interceptedId = (String) request.getAttribute("interceptedId");
//        if ("false".equalsIgnoreCase(skipHash) || skipHash == null || "true".equalsIgnoreCase(skipHash)) {
//            boolean verified = false;
//
//            if (request.getContentType() != null &&
//                    request.getContentType().startsWith("multipart/form-data") &&
//                    queryString != null && !queryString.isEmpty()) {
//
//                Map<String, String[]> parameterMap = request.getParameterMap();
//                Map<String, String> formFields = new LinkedHashMap<>();
//                // Extract query parameter keys from URL
//                Set<String> queryParamKeys = new HashSet<>();
//                if (queryString != null) {
//                    Arrays.stream(queryString.split("&"))
//                            .map(param -> param.split("=")[0])
//                            .forEach(queryParamKeys::add);
//                }
//
//                // Filter out query parameters
//                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//                    String key = entry.getKey();
//                    if (!queryParamKeys.contains(key)) {
//                        String[] value = entry.getValue();
//                        if (value != null && value.length > 0) {
//                            formFields.put(key, value[0]); // assuming single value
//                        }
//                    }
//                }
//
//                // Join as string
//                String formDataPart = formFields.entrySet().stream()
//                        .map(e -> e.getKey() + "=" + e.getValue())
//                        .collect(Collectors.joining("&"));
//
//
//                String queryPart = queryString != null ? queryString : "";
//                String combined = formDataPart + queryPart + reqMilliSec;
//                calculatedHmac = calculateHmac(combined, SECRET_KEY);
//
//            }else if (requestBody != null && !requestBody.isEmpty() && !requestBody.equals("{}") && queryString != null && !queryString.isEmpty()) {
//                // 🔐 Case 1: Payload + Query Params
//                String combined = requestBody + queryString + reqMilliSec;
//                calculatedHmac = calculateHmac(combined, SECRET_KEY);
//
//            } else if (requestBody != null && !requestBody.isEmpty() && !requestBody.equals("{}")) {
//                // 🔐 Case 2: Payload only
//                String combined = requestBody + reqMilliSec;
//                calculatedHmac = calculateHmac(combined, SECRET_KEY);
//
//            } else if (queryString != null && !queryString.isEmpty()) {
//                // 🔐 Case 3: Query Params only
//                String combined = queryString + reqMilliSec;
//                calculatedHmac = calculateHmac(combined, SECRET_KEY);
//
//            } else if (interceptedId != null && !interceptedId.isEmpty()) {
//                // 🔐 Case 4: Path Param only (like /getById/123)
//                String combined = interceptedId + reqMilliSec;
//                calculatedHmac = calculateHmac(combined, SECRET_KEY);
//            }
//
//            if (calculatedHmac!= null && !Objects.equals(calculatedHmac,receivedHmac)) {
//                wrappedResponse.resetBuffer();
//                wrappedResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                wrappedResponse.setContentType("application/json");
//                wrappedResponse.getWriter().write("{\"error\":\"Invalid request HMAC\"}");
//                wrappedResponse.copyBodyToResponse();
//                return;
//            }
//        }

        // ✅ Extract and sign response
        byte[] contentAsBytes = wrappedResponse.getContentAsByteArray();
        String responseBody = new String(contentAsBytes, response.getCharacterEncoding());

        long currentMillis = System.currentTimeMillis();
        String responseWithMillis = responseBody + currentMillis;
        String responseHmac = calculateHmac(responseWithMillis, SECRET_KEY);

        // Add headers
        wrappedResponse.setHeader("X-RESPONSE-HMAC", responseHmac);
        wrappedResponse.setHeader("X-currentMillis", String.valueOf(currentMillis));

        // Send updated response
        wrappedResponse.copyBodyToResponse();
    }

    private String extractBody(ContentCachingRequestWrapper request) throws IOException {
        // Force content to be cached by reading the input stream
        ServletInputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[1024];
        while (inputStream.read(buffer) != -1) {
            // reading to trigger caching, no-op
        }

        byte[] buf = request.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, request.getCharacterEncoding()) : "";
    }


    private String calculateHmac(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC", e);
        }
    }
}