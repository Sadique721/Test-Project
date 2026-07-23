package com.savbill.partnermanagement;

import com.savbill.partnermanagement.kafka.KafkaMessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class PartnerManagementApplication {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;
	public static void main(String[] args) {
		SpringApplication.run(PartnerManagementApplication.class, args);
		System.out.println("*************SavbillPartnerManagement Microservice Application Started Successfully****************");
	}

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}
}