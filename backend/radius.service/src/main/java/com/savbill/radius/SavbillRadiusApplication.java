package com.savbill.radius;

import com.savbill.radius.kafka.KafkaMessageReceiver;
import com.savbill.radius.services.impl.CacheConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication 
@EnableDiscoveryClient
@EnableScheduling
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@EnableFeignClients
public class SavbillRadiusApplication {
	@Autowired
	private CacheConfigServiceImpl cacheConfigService;

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

	public static void main(String[] args) {
		SpringApplication.run(SavbillRadiusApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Reload cache
		cacheConfigService.reloadCache();
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}
}
