package com.savbill.revenuemanagement.core.service;


import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.common.GenericSearchModel;
import com.savbill.revenuemanagement.core.entity.debitdoc.ExportInvoiceAudit;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.ExportInvoiceAuditRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceThread;
import com.savbill.revenuemanagement.core.threads.InvoiceExportThread;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.threadconfig.CustomThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ExportInvoiceService extends PostpaidInvoiceThread {

    private static final Logger logger = LoggerFactory.getLogger(ExportInvoiceService.class);
    @Autowired
    DebitDocRepository debitDocRepository;
    @Autowired
    KafkaMessageSender kafkaMessageSender;
    @Autowired
    ExportInvoiceAuditRepository exportInvoiceAuditRepository;
    @Value(value = "${instanceId}")
    private String instanceId;
    @Autowired
    private PdfUtil pdfUtil;

    CustomThreadPool threadPool = new CustomThreadPool(2,
            2, 60, TimeUnit.SECONDS, "invoice-export-th");

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public void startInvoicePdfThread(boolean isFromSchedular, Integer mvnoId) {
        try {
            ExportInvoiceAudit exportInvoiceAudit = new ExportInvoiceAudit();
            String requestId = UUID.randomUUID().toString();
            exportInvoiceAudit.setRequestId(requestId);
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.PENDING);
            exportInvoiceAudit.setSubmittedDate(LocalDateTime.now());
            exportInvoiceAudit.setUsername(isFromSchedular ? "Invoice_Export from Schedular" :getLoggedInUser().getUsername());
            exportInvoiceAudit.setMvnoId(isFromSchedular ? 0 : getLoggedInUser().getMvnoId());
            exportInvoiceAudit.setThreadName(isFromSchedular ? Constants.SCHEDULERS_NAME.AUTO_INVOICE_PDF : Constants.SCHEDULERS_NAME.MANUAL_INVOICE_PDF_GENERATE);
            exportInvoiceAuditRepository.save(exportInvoiceAudit);
            InvoiceExportThread invoiceExportThread = new InvoiceExportThread(this, requestId, mvnoId);
            threadPool.executeTask(invoiceExportThread);
            ApplicationLogger.logger.info(":::::::::::::::::::::: Started Invoice Pdf Generation ::::::::::::::::::::::");
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("::::::::::::::::::: Throws Exception during generate pdf for invoices. " + e.getMessage());
            throw new RuntimeException("Throws Exception during generate pdf for invoices. " + e.getMessage());
        }
    }

    public void startInvoiceNotificationThread(boolean isFromSchedular, Integer mvnoId) {
        try {
            ExportInvoiceAudit exportInvoiceAudit = new ExportInvoiceAudit();
            String requestId = UUID.randomUUID().toString();
            exportInvoiceAudit.setRequestId(requestId);
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.PENDING);
            exportInvoiceAudit.setSubmittedDate(LocalDateTime.now());
            exportInvoiceAudit.setUsername(isFromSchedular ? "Invoice_Distribution from Schedular" :getLoggedInUser().getUsername());
            exportInvoiceAudit.setMvnoId(isFromSchedular ? 0 : getLoggedInUser().getMvnoId());
            exportInvoiceAudit.setThreadName(isFromSchedular ? Constants.SCHEDULERS_NAME.AUTO_INVOICE_NOTIFICATION:Constants.SCHEDULERS_NAME.MANUAL_INVOICE_NOTIFICATION);
            exportInvoiceAuditRepository.save(exportInvoiceAudit);
            InvoiceDistributionThread invoiceDistributionThread = new InvoiceDistributionThread(this, requestId, mvnoId);
            threadPool.executeTask(invoiceDistributionThread);
            ApplicationLogger.logger.info(":::::::::::::::::::::: Started Invoice Pdf Distribution ::::::::::::::::::::::");
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("::::::::::::::::::: Throws Exception during sending invoices notification. " + e.getMessage());
            throw new RuntimeException("Throws Exception during sending invoices notification :: " + e.getMessage());
        }
    }

    public Page<ExportInvoiceAudit> getsearchexportinvoiceaudit(List<GenericSearchModel> filterList, Date fromdate,Date todate,String filterBy, Pageable pageable) {
        try {
            Specification<ExportInvoiceAudit> spec = filter(filterList,fromdate,todate,getLoggedInUser().getMvnoId(),filterBy);
            return exportInvoiceAuditRepository.findAll(spec,pageable);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated ExportInvoiceAudit", e);
        }
    }

    public static Specification<ExportInvoiceAudit> filter(List<GenericSearchModel> filters,Date fromdate,Date todate,Integer mvnoId,String filterBy) {
        return (Root<ExportInvoiceAudit> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("mvnoId"), mvnoId));
            ZoneId zone = ZoneId.systemDefault();

            if (fromdate != null && todate == null) {
                LocalDateTime fromDateTime = fromdate.toInstant().atZone(zone).toLocalDate().atStartOfDay();
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("executionStartDate"), fromDateTime));
            }

            // Handle todate only
            if (todate != null && fromdate == null) {
                LocalDateTime toDateTime = todate.toInstant().atZone(zone).toLocalDate().atTime(23, 59, 59, 999_999_999);
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("executionEndDate"), toDateTime));
            }

            // Handle both dates
            if (fromdate != null && todate != null) {
                LocalDateTime fromDateTime = fromdate.toInstant().atZone(zone).toLocalDate().atStartOfDay();
                LocalDateTime toDateTime = todate.toInstant().atZone(zone).toLocalDate().atTime(23, 59, 59, 999_999_999);
                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(root.get("executionStartDate"), fromDateTime),
                        cb.lessThanOrEqualTo(root.get("executionEndDate"), toDateTime)
                );
            }
            if (filterBy != null && !filterBy.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("threadName")), filterBy.toLowerCase()));
            }

            for (GenericSearchModel filter : filters) {
                String column = filter.getFilterColumn();
                String value = filter.getFilterValue();

                if (column == null || value == null) continue;

                switch (column.toLowerCase()) {
                    case "status":
                        predicate = cb.and(predicate, cb.equal(cb.lower(root.get("status")), value.toLowerCase()));
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported filter column: " + column);
                }
            }

            return predicate;
        };
    }

    public Page<ExportInvoiceAudit> getAllexportinvoiceaudit(Pageable pageable, Boolean isExport) {
        try {
            Page<ExportInvoiceAudit> paginationList = null;
            String threadName = null;
            if(null != isExport && isExport){
                threadName = "Invoice_Export";
            } else {
                threadName = "Invoice_Distribution";
            }
            if (getLoggedInUser().getMvnoId() == 1)
                paginationList = exportInvoiceAuditRepository.findAll(pageable, threadName);
            else
                paginationList = exportInvoiceAuditRepository.findAll(pageable, Arrays.asList(getLoggedInUser().getMvnoId(), 1), threadName);
            return paginationList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated ExportInvoiceAudit", e);
        }
    }
}

