package com.savbill.commonGateway;

import brave.Tracer;
import com.savbill.commonGateway.kafka.service.KafkaTopicCreation;
import com.savbill.commonGateway.security.filter.MyFilter;
import com.savbill.commonGateway.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableSwagger2
@EnableScheduling
@EnableZuulProxy
@EnableDiscoveryClient
@EnableFeignClients
public class CommonGatewayApplication {

	@Autowired
	private Tracer tracer;



	public static void main(String[] args) {
		SpringApplication.run(CommonGatewayApplication.class, args);
		System.out.println("*************Common API GateWay Application Started Successfully****************");
		KafkaTopicCreation kafkaTopicCreation = ApplicationContextProvider.getApplicationContext().getBean(KafkaTopicCreation.class);
		kafkaTopicCreation.createKafkaTopics();

	}


	@Bean
	public FilterRegistrationBean<MyFilter> myFilterRegistration() {
		FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new MyFilter(tracer));
		filterRegistrationBean.addUrlPatterns("/SavbillRadius/*", "/SavbillNotification/*", "/api/v1/SavbillSalesCrmsBss/*", "/api/v1/*", "/SavbillTaskMgmt/*","/api/v1/SavbillIntegrationSystem/*","/api/v1/KpiManagement/*","/api/v1/SavbillSample/*","/api/v1/TicketManagement/*","/api/v1/SavbillInventoryManagement/*","/api/v1/cpm/*","/api/v1/Revenue/*","/api/v1/pms/*,/api/v1/SavbillNetConfManagement/*","/api/v1/service-manager/*","/api/v1/index-coordination-node/*","/api/v1/search-agent/*","/api/v1/indexer-agent/*","/api/v1/enrichment/*","/api/v1/collection-agent/*","/api/v1/enrichment-lite/*","/SavbillDiameter/*", "/api/v1/log-search-agent/*", "/api/v1/log-writer-agent/*","/api/v1/TaskManagement/*");
		return filterRegistrationBean;
	}
}
