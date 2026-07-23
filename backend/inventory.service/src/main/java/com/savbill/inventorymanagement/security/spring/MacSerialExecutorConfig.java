package com.savbill.inventorymanagement.security.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class MacSerialExecutorConfig {

    @Bean(name = "macSerialUploadExecutor")
    public Executor macSerialUploadExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);       // safe default
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("mac-serial-upload-");

        executor.initialize();
        return executor;
    }

    @Bean(name = "outwardUploadExecutor")
    public Executor outwardUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("OutwardUpload-");
        executor.initialize();
        return executor;
    }
}

