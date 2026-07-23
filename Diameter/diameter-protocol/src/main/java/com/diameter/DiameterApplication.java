package com.diameter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.diameter.kafka.KafkaMessageReceiver;

@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@EnableScheduling
public class DiameterApplication {

    @Autowired
    KafkaMessageReceiver kafkaMessageReceiver;

    public static void main(String[] args) {
        System.setProperty("org.jdiameter.Configuration.validateXML", "false");
        
        SpringApplication.run(DiameterApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Use ExecutorService to manage the thread pool
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(kafkaMessageReceiver);
    }

}