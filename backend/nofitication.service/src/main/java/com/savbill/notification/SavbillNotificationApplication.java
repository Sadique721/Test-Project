package com.savbill.notification;

import com.savbill.notification.kafka.KafkaMessageReceiver;
import org.snmp4j.smi.UdpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.savbill.notification.snmp.SNMPConfig;
import com.savbill.notification.snmp.SNMPTrapGenerator;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication 
@EnableDiscoveryClient
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SavbillNotificationApplication {

	@Autowired
	KafkaMessageReceiver kafkaMessageReceiver;

    public static void main(String[] args) {
	SpringApplication.run(SavbillNotificationApplication.class, args);
	SNMPTrapGenerator trapGenerator = new SNMPTrapGenerator();
	trapGenerator.serverUpV2("public","127.0.0.1",9092,"Notification Server Up",".1.3.6.1.6.3.1.1.5.4");
	SNMPConfig snmpConfig=new SNMPConfig();
	try {
		//log.info("***** Savbill SNMP Starting *****");
		snmpConfig.listen(new UdpAddress("0.0.0.0/165"));
		//log.info("***** Savbill SNMP Started *****");
	}
	catch(Exception e) {
		e.printStackTrace();
	}
    }

	@PostConstruct
	public void init() {
		// Use ExecutorService to manage the thread pool
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaMessageReceiver);
	}

}
