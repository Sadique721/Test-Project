package com.savbill.radius.CronJobs;

import com.savbill.radius.aaa.snmp.SNMPCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Component
@EnableScheduling
public class SNMPCounterResetJob {

    private static final Logger log = LoggerFactory.getLogger(SNMPCounterResetJob.class);

    @Scheduled(cron = "${snmp.counter.reset.schedule}")
    public void cronJobForResetSNMPCounter() {
        try {
            log.debug(String.format("Cron job start for reset SNMP counter: %s ", LocalDateTime.now()));
            SNMPCounters snmpCounters = new SNMPCounters();
            snmpCounters.initializedResetStartDate();
            log.debug(String.format("Cron job end for reset SNMP counter: %s ", LocalDateTime.now()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
