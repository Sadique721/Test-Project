package com.diameter.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {


    @Bean(name = "auditExecutor")
    public Executor auditExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);

        executor.setQueueCapacity(50000);

        executor.setThreadNamePrefix(
                "diameter-audit-"
        );

        executor.initialize();

        return executor;
    }
}
