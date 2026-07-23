package com.savbill.inventorymanagement;

import com.savbill.inventorymanagement.kafka.KafkaMessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
public class InventoryManagementApplication {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApplication.class, args);
		System.out.println("*************Inventory Management Application Started Successfully****************");
	}

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}
}
