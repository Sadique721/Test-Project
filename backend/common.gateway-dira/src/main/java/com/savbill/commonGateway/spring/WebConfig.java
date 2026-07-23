package com.savbill.commonGateway.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	//registry.addMapping("/**");
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").exposedHeaders("X-RESPONSE-HMAC").exposedHeaders("X-currentMillis");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String remoteAddr = request.getHeader("X-Forwarded-For");
                if (StringUtils.hasText(remoteAddr)) {
                    remoteAddr = remoteAddr.split(",")[0].trim();
                } else {
                    remoteAddr = request.getRemoteAddr();
                }
                request.setAttribute("clientIp", remoteAddr);

                if (!request.getParameterMap().isEmpty()) {
//                    return true; // Skip the rest of the interceptor logic
                    request.setAttribute("interceptedId" , null);
                }

                Map<String, String> pathVariables =
                        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                if (pathVariables != null ) {
                    for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        request.setAttribute("interceptedId" , value);
                    }
                }

                return true;
            }
        });
    }
}
