package com.savbill.partnermanagement.swagger;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer{

    public static final String AUTHORIZATION_HEADER = "Authorization";
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("controller")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.savbill"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(actuatorSecurityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Savbill BSS Partner Management").description("Savbill BSS Partner Management").build();
    }

    @Bean
    public Docket moduleApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("modules")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.savbill"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(moduleApiInfo())
                .securityContexts(Arrays.asList(actuatorSecurityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiInfo moduleApiInfo() {
        return new ApiInfoBuilder().title("OSS/BSS Manager Modules").description("OSS/BSS Manager Module API").build();
    }

    private SecurityContext actuatorSecurityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.ant("/api/**"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("JWT", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }
}
