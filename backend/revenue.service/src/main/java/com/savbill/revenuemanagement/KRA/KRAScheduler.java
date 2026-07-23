package com.savbill.revenuemanagement.KRA;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class KRAScheduler {

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private KRAUtils kraUtils;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    private static final Logger logger = LoggerFactory.getLogger(KRAScheduler.class);

    @Scheduled(cron = "${cronJobTimeForKraInvoice}")
    public void kraInvoiceSyncScheduler() {
        logger.info("XXXXXXXXXXXX----------KRA Invoice Sync Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_KRA_INVOICE_SYNC);
        
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.KRA_INVOICE_SYNC)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.KRA_INVOICE_SYNC);
            try {
                List<DebitDocument> unsyncedInvoices = debitDocRepository.findUnsyncedKraInvoices();
                if (unsyncedInvoices != null && !unsyncedInvoices.isEmpty()) {
                    kraUtils.processEtimsAddInvoice(unsyncedInvoices);
                    logger.info("Processed {} unsynced KRA invoices", unsyncedInvoices.size());
                } else {
                    logger.info("No unsynced KRA invoices found.");
                }

                logger.info("XXXXXXXXXXXX----------KRA Invoice Sync Scheduler END---------XXXXXXXXXXXX");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("KRA Invoice Sync Scheduler Run Successfully");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********KRA Invoice Sync Scheduler Error***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.KRA_INVOICE_SYNC);
                logger.info("XXXXXXXXXXXX---------- KRA Invoice Sync Scheduler Lock released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("KRA Invoice Sync Scheduler Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX---------- KRA Invoice Sync Scheduler Lock held by another instance ---------XXXXXXXXXXXX");
        }
    }

    @Scheduled(cron = "${cronJobTimeForKraCreditNote}")
    public void kraCreditNoteSyncScheduler() {
        logger.info("XXXXXXXXXXXX----------KRA Credit Note Sync Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_KRA_CREDIT_NOTE_SYNC);
        
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.KRA_CREDIT_NOTE_SYNC)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.KRA_CREDIT_NOTE_SYNC);
            try {
                List<CreditDocument> unsyncedCreditNotes = creditDocRepository.findUnsyncedKraCreditNotes();
                if (unsyncedCreditNotes != null && !unsyncedCreditNotes.isEmpty()) {
                    kraUtils.processEtimsAddCreditNote(unsyncedCreditNotes);
                    logger.info("Processed {} unsynced KRA credit notes", unsyncedCreditNotes.size());
                } else {
                    logger.info("No unsynced KRA credit notes found.");
                }

                logger.info("XXXXXXXXXXXX----------KRA Credit Note Sync Scheduler END---------XXXXXXXXXXXX");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("KRA Credit Note Sync Scheduler Run Successfully");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********KRA Credit Note Sync Scheduler Error***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.KRA_CREDIT_NOTE_SYNC);
                logger.info("XXXXXXXXXXXX---------- KRA Credit Note Sync Scheduler Lock released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("KRA Credit Note Sync Scheduler Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX---------- KRA Credit Note Sync Scheduler Lock held by another instance ---------XXXXXXXXXXXX");
        }
    }
}
