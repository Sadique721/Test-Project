package com.savbill.taskmanagement;

import com.savbill.taskmanagement.kafka.KafkaMessageReceiver;
import com.savbill.taskmanagement.snmp.SNMPTrapGenerator;
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

//@SpringBootApplication
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableSwagger2
@EnableScheduling
@EnableZuulProxy
@EnableDiscoveryClient
public class TaskManagementApplication {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

	public static void main(String[] args) {

		SpringApplication.run(TaskManagementApplication.class, args);

		SNMPTrapGenerator trapV2 = new SNMPTrapGenerator();
		trapV2.clearTrap_Version2("public", "127.0.0.1", 162, "SM Server Started", ".1.3.6.1.2.1.1.10");
		System.out.println("SNMP V2c");


		System.out.println("*************Task Management Application Started Successfully****************");
	}

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}

}
