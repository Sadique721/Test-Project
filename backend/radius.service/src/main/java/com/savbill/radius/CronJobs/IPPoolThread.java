package com.savbill.radius.CronJobs;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.entity.Client;
import com.savbill.radius.entity.SchedularAudit;
import com.savbill.radius.ippool.domain.IPPoolMapping;
import com.savbill.radius.ippool.repository.IPPoolAllocationRepository;
import com.savbill.radius.services.SchedularAuditService;
import com.savbill.radius.utils.RadiusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class IPPoolThread implements Runnable {

    private final Client client;
    private final IPPoolAllocationRepository ipPoolDtlsRepository;

    private final SchedularAuditService schedularAuditService;

    private static final Logger log = LoggerFactory.getLogger(IPPoolThread.class);

    public IPPoolThread(IPPoolAllocationRepository ipPoolDtlsRepository, Client client , SchedularAuditService schedularAuditService) {
        this.ipPoolDtlsRepository = ipPoolDtlsRepository;
        this.schedularAuditService = schedularAuditService;
        this.client = client;
    }

    @Override
    public void run() {
        SchedularAudit schedularAudit = new SchedularAudit();
        schedularAudit.setSchedularName(AAAConstant.SCHEDULAR_IPPOOLTHREAD_NAME);
        schedularAudit.setStartTime(LocalDateTime.now());
        try {
            List<Long> ipPoolIds = client.getIpPoolMappingList().stream().map(IPPoolMapping::getIpPoolId).collect(Collectors.toList());
            int noForReserved = ipPoolDtlsRepository.releaseIpBasedOnStatusAndIdleTimeOut(ipPoolIds, client.getIdleTimeout(), RadiusConstants.RESERVED);
            int noForAllocated = ipPoolDtlsRepository.releaseIpBasedOnStatusAndIdleTimeOut(ipPoolIds, (client.getIdleTimeout() * 2), RadiusConstants.ALLOCATED);
            log.debug("No of IP Released for Reserved status and idle timeout: " + noForReserved + " \nNo of IP Released for Allocated status and idle timeout: " + noForAllocated);
            // Set success-related values
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setTotalCount(ipPoolIds.size());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_SUCCESS);
            schedularAudit.setDescription(client.getClientIpAddress());
        }
        catch (Exception e){
            // Set failure-related values
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_FAILURE);
            schedularAudit.setDescription(e.getMessage());
        }
        finally {
            // Save the audit record
            schedularAuditService.saveEntity(schedularAudit);
        }
    }

}
