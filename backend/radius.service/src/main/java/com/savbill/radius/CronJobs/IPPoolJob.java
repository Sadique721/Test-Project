package com.savbill.radius.CronJobs;

import com.savbill.radius.entity.Client;
import com.savbill.radius.ippool.repository.IPPoolAllocationRepository;
import com.savbill.radius.repository.ClientRepository;
import com.savbill.radius.repository.IPPoolMappingRepository;
import com.savbill.radius.services.SchedularAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IPPoolJob {
    @Autowired
    private IPPoolMappingRepository ipPoolMappingRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private IPPoolAllocationRepository ipPoolDtlsRepository;

    @Autowired
    private SchedularAuditService schedularAuditService;

    private static final Logger log = LoggerFactory.getLogger(IPPoolJob.class);

    @Scheduled(cron = "${ip.pool.cron.schedule}")
    public void cronJobToFreeIdleIPsFromIPPool() {
        log.info(String.format("Cron job run for IP-Pool Management at: %s", LocalDateTime.now()));
        //TODO: We are creating Threads for each client, and these are executed at same time so these query stuck or take more time to execute
        List<Long> clientIds = ipPoolMappingRepository.findDistinctClientId();
        for (Long clientId : clientIds) {
            Client client = clientRepository.findByClientId(clientId);
            Thread clientJob = new Thread(new IPPoolThread(ipPoolDtlsRepository, client,schedularAuditService));
            clientJob.setName(client.getClientIpAddress());
            clientJob.start();
        }

    }

}
