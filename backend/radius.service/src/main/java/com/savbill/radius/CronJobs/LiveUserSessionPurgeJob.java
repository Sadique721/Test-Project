package com.savbill.radius.CronJobs;

import com.savbill.radius.entity.Client;
import com.savbill.radius.repository.ClientRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.services.SchedularAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LiveUserSessionPurgeJob {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Autowired
    private LiveUserService liverUserService;

    @Autowired
    private SchedularAuditService schedularAuditService;
    private Integer counter = 0;

    private static final Logger log = LoggerFactory.getLogger(LiveUserSessionPurgeJob.class);

    @Scheduled(cron = "${session.prune.cron.schedule}")
    public void cronJobToPruneLiveSession() {
        log.info(String.format("Cron job run for Session Prune for live users started  at: %s no of times %s", LocalDateTime.now(), ++counter));
        //TODO: We are creating Threads for each client, and these are executed at same time so these query stuck or take more time to execute
        List<Client> clientIds = clientRepository.findAll();
        for (Client client : clientIds) {
            if (client.getSessionPurgeInterval() != null && client.getSessionPurgeInterval() != 0L) {
                log.debug("In Scheduler to purge live session for client: " + client.getClientIpAddress());
                Thread clientJob = new Thread(new ClientThread(client, liveUserRepository, liverUserService, schedularAuditService));
                clientJob.setName(client.getClientIpAddress());
                clientJob.start();
            }
        }
        log.debug(String.format("Cron job run for Session Prune for live users completed at: %s no of times %s", LocalDateTime.now(), counter));
    }
}
