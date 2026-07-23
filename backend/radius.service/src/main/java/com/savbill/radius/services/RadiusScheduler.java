package com.savbill.radius.services;


import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="spring.enable.scheduling")
public class RadiusScheduler {

//	@Scheduled(cron = "${scheduling.job.cron}")
//	public void run() {
//		final Logger log = LoggerFactory.getLogger(RadiusScheduler.class);
//		log.debug("***** Savbill RadiusScheduler Starting *****");
//		// usecases
//		log.debug("***** Savbill RadiusSchedulerr End *****");
//	}
}
