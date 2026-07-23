package com.diameter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

@Configuration
public class LoggingConfig {
	
	@Autowired
    private LoggingSystem loggingSystem;
	
	@Value("${logging.level.root}")
    private String rootLogLevel;
	
	@Value("${logging.level.stack}")
    private String stackLogLevel;
	
	@Value("${logging.level.com.diameter}")
    private String diameterLogLevel;

    @Bean
    public ApplicationRunner setLogLevelAtStartup() {
        return args -> {
            loggingSystem.setLogLevel("root", LogLevel.valueOf(rootLogLevel));
            loggingSystem.setLogLevel("com.diameter", LogLevel.valueOf(diameterLogLevel));
            loggingSystem.setLogLevel("STACK", LogLevel.valueOf(stackLogLevel));
            loggingSystem.setLogLevel("CORE", LogLevel.valueOf(stackLogLevel));
        };
    }
}
