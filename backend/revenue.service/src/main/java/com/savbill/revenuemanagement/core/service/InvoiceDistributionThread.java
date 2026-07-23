package com.savbill.revenuemanagement.core.service;


import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.debitdoc.ExportInvoiceAudit;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.ExportInvoiceAuditRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.server.CustomerData;
import com.savbill.revenuemanagement.utils.ApplicationContextProvider;
import lombok.NoArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Service
public class InvoiceDistributionThread implements Runnable{
    private static final Log logger = LogFactory.getLog(InvoiceDistributionThread.class);

    private KafkaMessageSender kafkaMessageSender;
    private PdfUtil pdfUtil;
    ExportInvoiceAuditRepository exportInvoiceAuditRepository;
    DebitDocRepository debitDocRepository;
    private ExportInvoiceService exportInvoiceService;
    private CustomersRepository customersRepository;

    private String requestId;

    private AtomicInteger count;

    private Integer mvnoId;

    List<Future<Boolean>> taskflags = new ArrayList<>();

    public InvoiceDistributionThread(ExportInvoiceService exportInvoiceService, String requestId, Integer mvnoId) {
        this.exportInvoiceService = exportInvoiceService;
        this.requestId = requestId;
        this.exportInvoiceAuditRepository = ApplicationContextProvider.getApplicationContext().getBean(ExportInvoiceAuditRepository.class);
        this.debitDocRepository = ApplicationContextProvider.getApplicationContext().getBean(DebitDocRepository.class);
        this.customersRepository = ApplicationContextProvider.getApplicationContext().getBean(CustomersRepository.class);
        this.pdfUtil = ApplicationContextProvider.getApplicationContext().getBean(PdfUtil.class);
        this.kafkaMessageSender = ApplicationContextProvider.getApplicationContext().getBean(KafkaMessageSender.class);
        this.mvnoId = mvnoId;
    }

    @Override
    public void run() {
        count = new AtomicInteger();
        logger.info(":::::::::::::::::::::: Invoice Distribution Thread Started On : currentDate : " + LocalDateTime.now());
        ExportInvoiceAudit exportInvoiceAudit = exportInvoiceAuditRepository.findByRequestId(requestId);
        try {
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.STARTED);
            exportInvoiceAudit.setExecutionStartDate(LocalDateTime.now());
            exportInvoiceAudit = exportInvoiceAuditRepository.save(exportInvoiceAudit);

            taskflags = generateInvoiceNotification(mvnoId);

            boolean allCompleted = areAllTasksCompleted(taskflags);

            if (allCompleted){
                System.out.println(":::::::::::::::::::: All Distribution thread completed :::::::::::::::::::::::::::::::  "+count.get());
                exportInvoiceAudit.setExportCount(count.get());
                exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.COMPLETED);
                exportInvoiceAudit.setExecutionEndDate(LocalDateTime.now());
                exportInvoiceAuditRepository.save(exportInvoiceAudit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.Failed);
            exportInvoiceAudit.setRemarks(getStackTraceAsString(e));
            exportInvoiceAudit.setExecutionEndDate(LocalDateTime.now());
            exportInvoiceAuditRepository.save(exportInvoiceAudit);
        }
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public List<Future<Boolean>> generateInvoiceNotification(Integer mvnoId) {
        List<Future<Boolean>> tasks = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            ApplicationLogger.logger.info(":::::::::::::::::::::: Started Sending Invoice ::::::::::::::::::::::");
            List<String> statusList = new ArrayList<>();
            statusList.add(Constants.DEBIT_DOC_STATUS.EXPORTED);
            List<Object[]> debitDocDetails = new ArrayList<>();
            if(mvnoId == null){
                debitDocDetails = debitDocRepository.findDebitDocDetailsByBillRunStatusIn(statusList);
            } else {
                debitDocDetails = debitDocRepository.findDebitDocDetailsByBillRunStatusInWithMvnoId(statusList, mvnoId);
            }
            ApplicationLogger.logger.warn(":::::::: Total size of Invoices which are not sent :::::::: " + debitDocDetails.size());
            if (!debitDocDetails.isEmpty()) {
                for (Object[] row : debitDocDetails) {
                    Integer debitDocId = null;
                    Integer customerId = null;
                    if (row[0] != null)
                        debitDocId = ((BigInteger) row[0]).intValue();
                    if (row[2] != null)
                        customerId = ((BigInteger) row[2]).intValue();
                    String docNumber = row[1] != null ? (String) row[1] : null;

                    PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
                    CustomerData customerData = customersRepository.findByCustomerId(customerId);
                    try {
                        prepaidInvoiceService.sendInvoiceEmailFromScheduler(debitDocId, docNumber, customerData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    debitDocRepository.updateDebitDocumentBillRunStatus(debitDocId, Constants.DEBIT_DOC_STATUS.DISTRIBUTED);
                    count.incrementAndGet();
                }
                ApplicationLogger.logger.warn("::::::::::::::::::: Time Taken for sending invoices ::::::::::::::::::: " + (System.currentTimeMillis() - startTime));
            } else {
                ApplicationLogger.logger.info("::::::::::::::::::: All Invoices are already Sent ::::::::::::::::::: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("::::::::::::::::::: Throws Exception during generate pdf for invoices. " + e.getMessage());
            throw new RuntimeException("Exception during sending invoice pdf " + e.getMessage());
        }
        return tasks;
    }

    private boolean areAllTasksCompleted(List<Future<Boolean>> taskFlags) {
        List<Boolean> isTasksCompleted = new ArrayList<>();
        for (Future<Boolean> booleanFuture : taskFlags) {
            try {
                boolean taskCompleted = booleanFuture.get(); // Wait with timeout
                // If the task was not cancelled and completed, add it to the list
                isTasksCompleted.add(!booleanFuture.isCancelled() && taskCompleted);
            } catch (InterruptedException interruptedException) {
                //log.warn("thread interrupted, reason is: {}", interruptedException.getMessage());
                // Handle the exception appropriately or log it.
                Thread.currentThread().interrupt();
                isTasksCompleted.add(false); // Mark task as not completed in case of an exception.
            } catch (ExecutionException executionException) {
                //log.warn("Exception in task: {}", executionException.getMessage());
                isTasksCompleted.add(false);
                booleanFuture.cancel(true); // If a task encounters an exception, cancel it
            } catch (CancellationException cancellationException) {
                //log.warn("Cancel exception occurred in task: {}", cancellationException.getMessage());
                isTasksCompleted.add(false);
                booleanFuture.cancel(true);
            }
        }
        return isTasksCompleted.stream().allMatch(b -> b);
    }
}
