package com.savbill.integrationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableSwagger2
@EnableScheduling
@EnableZuulProxy
@EnableFeignClients
@EntityScan("com.savbill.integrationsystem")
public class IntegrationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationSystemApplication.class, args);
        System.out.println("************** Application integration system  started successfully.******************");
    }
}
