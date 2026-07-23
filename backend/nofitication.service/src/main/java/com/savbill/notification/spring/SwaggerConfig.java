package com.savbill.notification.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.models.auth.In;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    private static final String AUTHORIZATION = "Authorization";
    private static final String SECURITY_REFERENCE = "Token Access";
    private static final String AUTHORIZATION_SCOPE = "Unlimited";
    private static final String AUTHORIZATION_DESCRIPTION = "Full API Permission";

    @Bean
    public Docket api() {
	return new Docket(DocumentationType.SWAGGER_2).select()
            .apis(RequestHandlerSelectors.any())
//		.apis(RequestHandlerSelectors.basePackage("com.savbill.notification.controller"))
		.paths(PathSelectors.any()).build().securityContexts(Arrays.asList(securityContext()))
		.securitySchemes(Arrays.asList(apiKey())).apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title("Savbill Notification Service").description("Savbill Notification Service").build();
    }

    private ApiKey apiKey() {
	return new ApiKey(SECURITY_REFERENCE, AUTHORIZATION, In.HEADER.name());
    }

    private SecurityContext securityContext() {
	return SecurityContext.builder().securityReferences(securityReference()).build();
    }

    private List<SecurityReference> securityReference() {
	AuthorizationScope[] authorizationScope = {
		new AuthorizationScope(AUTHORIZATION_SCOPE, AUTHORIZATION_DESCRIPTION) };
	return Collections.singletonList(new SecurityReference(SECURITY_REFERENCE, authorizationScope));
    }
}