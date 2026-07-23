package com.savbill.cpm.schedulers;

import com.savbill.cpm.core.dto.ConnectionNumberDto;

import com.savbill.cpm.modules.CronJobs.CronjobConst;

import com.savbill.cpm.repository.radius.CustomerServiceMappingRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.schedulerAudit.SchedulerAudit;
import com.savbill.cpm.schedulerAudit.SchedulerAuditService;
import com.savbill.cpm.service.CustomerThreadService;
import com.savbill.cpm.service.SchedulerLockService;
import com.savbill.cpm.utils.NumberSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class CustomerScheduler {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    CustomerThreadService customerThreadService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;


    @Scheduled(cron = "${cronjobtimeforupdatecustomeranditsservice}}")
    public void updateCustomerScheduler() throws InterruptedException {
        log.info("XXXXXXXXXXXX----------CRON Update Customer And Service Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.UPDATE_CUSTOMER_AND_SERVICE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE);
            try {
                Thread thread = new Thread(customerThreadService.newRunnable(), "T1");
                thread.run();

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Update Customer And Service Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(null);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE);
                log.info("XXXXXXXXXXXX---------- Update Customer And Service Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Update Customer And Service Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Update Customer And Service Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    @Scheduled(cron = "${cronjobtimeforconnectiongenerate}")
    public void ConnectionNumberGanrate() throws InterruptedException {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_CONNECTION_NUMBER_GENERATE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_PLAN_CONNECTION_NUMBER_GENERATE_SCHEDULER);

        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE);
            try {
                List<ConnectionNumberDto> customers = customerServiceMappingRepository.findCustomersWithNullOrEmptyConnectionNumber();
                for (ConnectionNumberDto row : customers) {
                    Integer mappingId = row.getCustServiceMappingId();
                    Integer mvnoId = row.getMvnoId();
                    Integer partnerId = (row.getPartnerId() != null) ? row.getPartnerId() : null;
                    Integer lcoId = customersRepository.findLcoIdByCustId(row.getCustomerId());

                    boolean isLCO = lcoId != null;
                    String newConnectionNo = numberSequenceUtil.getConnectionNumberGenerate(isLCO, partnerId, mvnoId);
                    log.info("Generating connection number for mappingId: {}, mvnoId: {}, partnerId: {}", mappingId, mvnoId, partnerId);
                    customerServiceMappingRepository.updateConnectionNumberByMappingId(mappingId, newConnectionNo, partnerId, mvnoId);
                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Connection Number Generate Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(customers.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error("**********Scheduler Showing ERROR***********");
                log.error(ex.toString(), ex);
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE);
                log.info("XXXXXXXXXXXX---------- Update Customer And Service Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Connection Number Generate Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Connection Number Generate Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}

