package com.savbill.revenuemanagement.core.threads;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.ExportInvoiceAudit;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.ExportInvoiceAuditRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ExportInvoiceService;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceCharges;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.threadconfig.CustomThreadPool;
import com.savbill.revenuemanagement.utils.ApplicationContextProvider;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Service
public class InvoiceExportThread implements Runnable {

    private static final Log logger = LogFactory.getLog(InvoiceExportThread.class);
    private KafkaMessageSender kafkaMessageSender;
    private PdfUtil pdfUtil;
    ExportInvoiceAuditRepository exportInvoiceAuditRepository;
    DebitDocRepository debitDocRepository;
    private ExportInvoiceService exportInvoiceService;
    private String requestId;

    private AtomicInteger count;

    private Integer mvnoId;

    List<Future<Boolean>> taskflags = new ArrayList<>();

    CustomThreadPool threadPool = new CustomThreadPool(10,
            10, 60, TimeUnit.SECONDS, "invoice-export-th");

    public InvoiceExportThread(ExportInvoiceService exportInvoiceService, String requestId, Integer mvnoId) {
        this.exportInvoiceService = exportInvoiceService;
        this.requestId = requestId;
        this.exportInvoiceAuditRepository = ApplicationContextProvider.getApplicationContext().getBean(ExportInvoiceAuditRepository.class);
        this.debitDocRepository = ApplicationContextProvider.getApplicationContext().getBean(DebitDocRepository.class);
        this.pdfUtil = ApplicationContextProvider.getApplicationContext().getBean(PdfUtil.class);
        this.kafkaMessageSender = ApplicationContextProvider.getApplicationContext().getBean(KafkaMessageSender.class);
        this.mvnoId = mvnoId;
    }

    @Override
    public void run() {
        count = new AtomicInteger();
        logger.info(":::::::::::::::::::::: Invoice Export Thread Started On : currentDate : " + LocalDateTime.now());
        ExportInvoiceAudit exportInvoiceAudit = exportInvoiceAuditRepository.findByRequestId(requestId);
        try {
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.STARTED);
            exportInvoiceAudit.setExecutionStartDate(LocalDateTime.now());
            exportInvoiceAudit = exportInvoiceAuditRepository.save(exportInvoiceAudit);
            taskflags = generateInvoicePdf(mvnoId);
            boolean allCompleted = areAllTasksCompleted(taskflags);


            if (allCompleted){
                System.out.println(":::::::::::::::::::: All Export thread completed :::::::::::::::::::::::::::::::  "+count.get());
                exportInvoiceAudit.setExportCount(count.get());
                exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.COMPLETED);
                exportInvoiceAudit.setExecutionEndDate(LocalDateTime.now());
                exportInvoiceAuditRepository.save(exportInvoiceAudit);
            }

        } catch (Exception e) {
            e.printStackTrace();
            exportInvoiceAudit.setStatus(Constants.THREAD_STATUS.Failed);
            exportInvoiceAudit.setRemarks(e.getMessage());
            exportInvoiceAudit.setExecutionEndDate(LocalDateTime.now());
            exportInvoiceAuditRepository.save(exportInvoiceAudit);
        }

    }

    private class ExportThread implements Callable<Boolean> {
        private AtomicInteger count;
        private Integer debitDocId;

        public ExportThread(AtomicInteger count, Integer debitDocId) {
            this.count = count;
            this.debitDocId = debitDocId;
        }

        @Override
        public Boolean call() {
            long startTime = System.currentTimeMillis();
            PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
            Optional<DebitDocument> debitDocumentOptional = debitDocRepository.findById(debitDocId);
            if(debitDocumentOptional.isPresent()){
                DebitDocument debitDocument = debitDocumentOptional.get();
                String xmlDocument = prepaidInvoiceService.setInvoiceXml(debitDocument);
                debitDocRepository.updateDebitDocumentXmlDocument(debitDocument.getId(), xmlDocument);
                boolean pdfGenerationFlag = false;
                try {
                    pdfGenerationFlag = pdfUtil.generatePDF(debitDocument, false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (pdfGenerationFlag) {
                    debitDocRepository.updateDebitDocumentBillRunStatus(debitDocument.getId(), Constants.DEBIT_DOC_STATUS.EXPORTED);
                    PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), null, null, null, null);
                    kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
                }
                ApplicationLogger.logger.warn("::::::::::::::::::: Time Taken for Generate ::::::::::::::::::: " + (System.currentTimeMillis() - startTime));
                count.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    public List<Future<Boolean>> generateInvoicePdf(Integer mvnoId) {
        List<Future<Boolean>> tasks = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            List<String> statusList = new ArrayList<>();
            statusList.add(Constants.DEBIT_DOC_STATUS.EXPORTED);
            statusList.add(Constants.DEBIT_DOC_STATUS.CANCELLED);
            statusList.add(Constants.DEBIT_DOC_STATUS.VOID);
            statusList.add(Constants.DEBIT_DOC_STATUS.DISTRIBUTED);
            List<Integer> debitDocIds = new ArrayList<>();
            if(mvnoId == null){
                debitDocIds = debitDocRepository.findAllDebitDocIdByBillRunStatusNotIn(statusList);
            } else {
                debitDocIds = debitDocRepository.findAllDebitDocIdByBillRunStatusNotInWithMvnoId(statusList, mvnoId);
            }
            if (!debitDocIds.isEmpty()) {
                for (Integer debitDocId : debitDocIds) {
                    tasks.add(threadPool.submitTask(new ExportThread(count, debitDocId)));
                }
                ApplicationLogger.logger.warn("::::::::::::::::::: Time Taken for Exporting Invoice Pdf ::::::::::::::::::: " + (System.currentTimeMillis() - startTime));
            } else {
                ApplicationLogger.logger.info("::::::::::::::::::: All Invoices are Exported ::::::::::::::::::: ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("::::::::::::::::::: Throws Exception during generate pdf for invoices. " + e.getMessage());
            throw new RuntimeException("Exception during generate invoice pdf " + e.getMessage());
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
                // Handle the exception appropriately or log it.
                Thread.currentThread().interrupt();
                isTasksCompleted.add(false); // Mark task as not completed in case of an exception.
            } catch (ExecutionException executionException) {
                isTasksCompleted.add(false);
                booleanFuture.cancel(true); // If a task encounters an exception, cancel it
            } catch (CancellationException cancellationException) {
                isTasksCompleted.add(false);
                booleanFuture.cancel(true);
            }
        }
        return isTasksCompleted.stream().allMatch(b -> b);
    }
}
