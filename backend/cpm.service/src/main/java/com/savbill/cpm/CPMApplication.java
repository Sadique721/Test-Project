package com.savbill.cpm;

import com.savbill.cpm.filter.MyFilter;
import com.savbill.cpm.kafka.KafkaMessageReceiver;
import com.savbill.cpm.modules.Communication.service.CommonCommunicationService;
import com.savbill.cpm.service.common.ClientServiceSrv;
import com.savbill.cpm.snmp.SNMPTrapGenerator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableSwagger2
@EnableScheduling
@EnableZuulProxy
@EnableDiscoveryClient
@EnableFeignClients
public class CPMApplication implements CommandLineRunner {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
 
    @Autowired
    CommonCommunicationService communicationService;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    KafkaMessageReceiver kafkaMessageReceiver;
    private static Logger log = LoggerFactory.getLogger(CPMApplication.class);
//    //HTTP port
//    @Value("${http.port}")
//    private int httpPort;
//
//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
//        return tomcat;
//    }
//
//    private Connector createStandardConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setPort(httpPort);
//        return connector;
//}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CPMApplication.class, args);

        SNMPTrapGenerator trapV2 = new SNMPTrapGenerator();
        trapV2.clearTrap_Version2("public", "127.0.0.1", 162, "SM Server Started", ".1.3.6.1.2.1.1.10");
        System.out.println("SNMP V2c");
        System.out.println("*************Application Started Successfully****************");
    }

    @Override
    public void run(String... args0) throws Exception {
        communicationService.previousSchedules();
    }
    
    @Bean 
    public FilterRegistrationBean<MyFilter> myFilterRegistration() {
        FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MyFilter());
        filterRegistrationBean.addUrlPatterns("/SavbillRadius/*", "/SavbillNotification/*", "/api/v1/SavbillSalesCrmsBss/*", "/api/v1/*", "/SavbillTaskMgmt/*","/SavbillIntegrationSystem/*","/api/v1/KpiManagement/*","/api/v1/SavbillSample/*","/api/v1/TicketManagement/*","/api/v1/SavbillInventoryManagement/*","/api/v1/SavbillApiGateWayCommon/*");
        return filterRegistrationBean;
    }

    @PostConstruct
    public void init() {
        // Use ExecutorService to manage the thread pool
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(kafkaMessageReceiver);
    }
}

