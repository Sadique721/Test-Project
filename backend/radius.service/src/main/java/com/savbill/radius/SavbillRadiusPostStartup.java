package com.savbill.radius;

import com.savbill.radius.aaa.db.DataSource;
import com.savbill.radius.aaa.server.RadiusAAAServer;
import com.savbill.radius.aaa.snmp.SNMPCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.UdpAddress;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SavbillRadiusPostStartup implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final Logger log = LoggerFactory.getLogger(SavbillRadiusPostStartup.class);


        SNMPCounters snmpCounters = new SNMPCounters();
        try {
            //init.setPropertiesInNonSpringClass();

            log.warn("***** Savbill Radius Service Starting *****");
            DataSource.setupDataSource();
            RadiusAAAServer radiusAAAServer = new RadiusAAAServer();
            radiusAAAServer.startServer();
            log.warn("***** Savbill Radius Service Started *****");

            log.warn("***** Savbill SNMP Starting *****");
            snmpCounters.listen(new UdpAddress("0.0.0.0/1615"));
            log.warn("***** Savbill SNMP Started *****");
        } catch (Exception e) {
            log.error("Error to start AAA Radius: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
