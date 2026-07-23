package com.savbill.salescrmsbss;

import com.savbill.salescrmsbss.kafka.KafkaMessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableDiscoveryClient
@EnableScheduling
@EnableSwagger2
@EnableZuulProxy
public class SalesCrmsBssApplication {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

	public static void main(String[] args) {
		SpringApplication.run(SalesCrmsBssApplication.class, args);
		System.out.println("*************Application Started Successfully**************** ");
	}

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}
}
