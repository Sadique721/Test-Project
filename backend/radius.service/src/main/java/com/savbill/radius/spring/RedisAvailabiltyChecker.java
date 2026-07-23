package com.savbill.radius.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisAvailabiltyChecker {

    private static final Logger logger = LoggerFactory.getLogger(RedisAvailabiltyChecker.class);

    @Bean
    public CommandLineRunner checkRedisAvailability(LettuceConnectionFactory factory) {
        return args -> {
            try {
                factory.getConnection().ping();
                logger.info("Redis is available.");
                System.out.println("Redis is available.");
            } catch (Exception e) {
                logger.warn("Redis is not available. Shutting down Redis-related beans.");
                System.out.println("Redis is not available. Shutting down Redis-related beans.");
                factory.destroy();
            }
        };
    }
}
