package com.savbill.revenuemanagement.scheduler;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.service.ExportInvoiceService;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * The type Postpaid manual payment adjustment scheduler.
 */
@Component
public class Schedulers {

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


    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private ExportInvoiceService exportInvoiceService;

    private static final Logger logger = LoggerFactory.getLogger(Schedulers.class);

    /**
     * Postpaid customer automate payment adjustment.
     */
    //@Scheduled(cron = "${cronjobforgenerateinvoicepdf}")
    public void cronjobforgenerateinvoicepdf() {
        logger.info("XXXXXXXXXXXX---------- Process Generate Invoice PDF Scheduler Start ---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.INVOICE_PDF_GENERATE);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.GENERATE_INVOICE_PDF)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.GENERATE_INVOICE_PDF);
            try {
                exportInvoiceService.startInvoicePdfThread(true, null);
                logger.info("XXXXXXXXXXXX---------- Process Generate Invoice PDF Scheduler END ---------XXXXXXXXXXXX");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Invoice PDF Generation Successfully");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("********** Process Generate Invoice PDF Scheduler Error ***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.GENERATE_INVOICE_PDF);
                logger.info("XXXXXXXXXXXX---------- Generate Invoice PDF Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Generate Invoice PDF Schedular Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX---------- Generate Invoice PDF Scheduler Locked held by another instance ---------XXXXXXXXXXXX");
        }
    }


    //@Scheduled(cron = "${cronjobforsendinvoicepdf}")
    public void cronjobforsendinvoicepdf() {
        logger.info("XXXXXXXXXXXX---------- Process Send Invoice PDF Scheduler Start ---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.INVOICE_PDF_GENERATE);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.SEND_INVOICE_PDF)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.SEND_INVOICE_PDF);
            try {
                exportInvoiceService.startInvoiceNotificationThread(true, null);
                logger.info("XXXXXXXXXXXX---------- Process Send Invoice PDF Scheduler END ---------XXXXXXXXXXXX");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Invoice PDF Send Successfully");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("********** Process Send Invoice PDF Scheduler Error ***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.SEND_INVOICE_PDF);
                logger.info("XXXXXXXXXXXX---------- Send Invoice PDF Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Send Invoice PDF Schedular Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX---------- Send Invoice PDF Scheduler Locked held by another instance ---------XXXXXXXXXXXX");
        }
    }
}
