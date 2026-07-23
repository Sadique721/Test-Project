package com.savbill.revenuemanagement;


import com.savbill.revenuemanagement.kafka.KafkaMessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients
public class SavbillRevenueManagement {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

	public static void main(String[] args) {
		SpringApplication.run(SavbillRevenueManagement.class, args);
		System.out.println("*************Application Started Successfully****************");
	}

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}
}
