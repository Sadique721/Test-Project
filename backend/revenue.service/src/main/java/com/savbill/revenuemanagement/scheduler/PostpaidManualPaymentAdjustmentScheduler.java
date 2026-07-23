package com.savbill.revenuemanagement.scheduler;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;

import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * The type Postpaid manual payment adjustment scheduler.
 */
@Component
public class PostpaidManualPaymentAdjustmentScheduler {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private SchedulerLockService schedulerLockService;

    //@Autowired
    // private MessageSender messageSender;

    /**
     * The Kafka message sender.
     */
    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;
    private static final Logger logger = LoggerFactory.getLogger(PostpaidManualPaymentAdjustmentScheduler.class);

    /**
     * Postpaid customer automate payment adjustment.
     */
    @Scheduled(cron = "${cronJobTimeForAutomatePayment}")
    public void postpaidCustomerAutomatePaymentAdjustment() {
        logger.info("XXXXXXXXXXXX----------Customer Automate Payment Adjustment Scheduler START---------XXXXXXXXXXXX");
         SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_Postpaid_Customer_Automate_Payment_Adjustment);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.AUTOMATE_PAYMENT)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.AUTOMATE_PAYMENT);
            try {
                subscriberService.adjustPrepaidCustomerDebitDocWithAdvancePayment();
                subscriberService.adjustPrepaidCustomerTrialDebitDocWithAdvancePayment();
                subscriberService.renewPrepaidCustomerWithAdvancePayment();
                subscriberService.renewWalletForBoosterPlan();
                logger.info("XXXXXXXXXXXX----------Customer Automate Payment Adjustment Scheduler END---------XXXXXXXXXXXX");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Customer Automate Payment Schedular Run Successfully");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Scheduler Error***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.AUTOMATE_PAYMENT);
                logger.info("XXXXXXXXXXXX---------- Customer Automate Payment Adjustment Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Customer Automate Payment Schedular Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX---------- Customer Automate Payment Adjustment Scheduler Locked held by another instance ---------XXXXXXXXXXXX");
        }
    }
}
