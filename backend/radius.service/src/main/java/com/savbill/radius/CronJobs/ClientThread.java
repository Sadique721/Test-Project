package com.savbill.radius.CronJobs;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.entity.Client;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.entity.SchedularAudit;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.services.SchedularAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ClientThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientThread.class);
    private final Client client;
    private final LiveUserRepository liveUserRepository;
    private LiveUserService liverUserService;

    private final SchedularAuditService schedularAuditService;

    public ClientThread(Client client, LiveUserRepository liveUserRepository, LiveUserService liverUserService, SchedularAuditService schedularAuditService) {
        this.client = client;
        this.liveUserRepository = liveUserRepository;
        this.liverUserService = liverUserService;
        this.schedularAuditService = schedularAuditService;
    }

    @Override
    public void run() {
        SchedularAudit schedularAudit = new SchedularAudit();
        try {
            schedularAudit.setStartTime(LocalDateTime.now());
            schedularAudit.setSchedularName(AAAConstant.SCHEDULAR_LIVE_USER_SESSION_PURGE_NAME);
            log.info("Session prune Job has been started for Client wit IP: " + client.getClientIpAddress());
            List<LiveUser> sessionIdToPurgeSessions = liveUserRepository.getLiveUsersToPurgeSession(client.getMvnoId(), client.getClientIpAddress(), client.getSessionPurgeInterval());
            log.info("Prune Live Session, Generating DM for listed lives users by id: " + sessionIdToPurgeSessions.size());
            if (!CollectionUtils.isEmpty(sessionIdToPurgeSessions)) {
                liverUserService.disconnectLiveUsersOfStaleSession(sessionIdToPurgeSessions, client.getMvnoId());
                log.debug("Session prune Job have been completed for Client wit IP: " + client.getClientIpAddress());
            } else {
                log.debug("NO Live user found for session purge client: " + client.getClientIpAddress() + ", time: " + LocalDateTime.now());
            }
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setDescription(client.getClientIpAddress());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_SUCCESS);
            schedularAudit.setTotalCount(sessionIdToPurgeSessions.size());
        }
        catch (Exception e){
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_FAILURE);
            schedularAudit.setDescription(e.getMessage());
        }
        finally {
            schedularAuditService.saveEntity(schedularAudit);
        }
    }
}
