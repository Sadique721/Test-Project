package com.diameter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.diameter.stack.DiameterStack;

@Configuration
public class DiameterStackBeanConfig {


    @Bean
    public DiameterStack diameterServerStack() {
        return new DiameterStack();
    }

}
