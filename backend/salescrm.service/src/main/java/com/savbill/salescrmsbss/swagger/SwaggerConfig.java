package com.savbill.salescrmsbss.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
	return new Docket(DocumentationType.SWAGGER_2)
			.groupName("controller")
			.select()
		.apis(RequestHandlerSelectors.basePackage("com.savbill.salescrmsbss.controller"))
		.paths(PathSelectors.any())
		.build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title("Sales CRMS BSS Service").description("Sales CRMS BSS Service").build();
    }
}
