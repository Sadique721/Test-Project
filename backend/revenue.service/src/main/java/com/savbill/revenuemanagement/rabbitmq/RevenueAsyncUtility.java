package com.savbill.revenuemanagement.rabbitmq;

import com.savbill.revenuemanagement.core.dto.invoice.PaymentListPojo;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.repository.BillRun.BillRunRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class RevenueAsyncUtility {
    static Executor xmlProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("INVOICE-%d").build());

    @Async
    public void DebitDocumentUpdateProcess(DebitDocument debitDocument,CustomerBillingMessage message,Integer mvnoId,Boolean isLco) {
        xmlProcessExecutor.execute(new XmlProcessThread(debitDocument, message, mvnoId, isLco));
    }
}


class XmlProcessThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RevenueAsyncUtility.class);

    DebitDocument debitDocument;

    CustomerBillingMessage message;

    Integer mvnoId = null;

    Boolean isLco = false;

    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;


    XmlProcessThread(DebitDocument debitDocument,CustomerBillingMessage message,Integer mvnoId,Boolean isLco){
        this.debitDocument=debitDocument;
        this.message=message;
        this.mvnoId=mvnoId;
        this.isLco=isLco;
    }

    public void run() {
        PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
        DebitDocRepository debitDocRepository=SpringContext.getBean(DebitDocRepository.class);

        String xml = prepaidInvoiceService.setInvoiceXml(debitDocument);
        DebitDocument invoice = debitDocument;
        if(invoice!=null) {
            invoice.setDocument(xml);
            if(message.getData().get(CustomerBillingMessage.BILL_RUN_ID) != null) {
                updateBillRunData(invoice, Integer.valueOf(message.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
                invoice.setBillrunid(Integer.valueOf(message.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
            }
            InvoiceUtil invoiceUtil=SpringContext.getBean(InvoiceUtil.class);
            if(invoice.getCustomer() != null && invoice.getCustomer().getCurrency() != null){
                String centCurrDynamic = invoiceUtil.getSubunitName(invoice.getCustomer().getCurrency());
                invoice.setTotalamountinwords(invoiceUtil.convertToAmount((invoice.getTotalamount() * 100) / 100 , invoice.getCustomer().getCurrency(), centCurrDynamic) + " Only");
                invoice.setTotaldueinwords(invoiceUtil.convertToAmount(invoice.getTotaldue(), invoice.getCustomer().getCurrency(), centCurrDynamic) + " Only");
            } else {
                invoice.setTotalamountinwords(invoiceUtil.convertToAmount((invoice.getTotalamount() * 100) / 100 , curr, centCurr) + " Only");
                invoice.setTotaldueinwords(invoiceUtil.convertToAmount(invoice.getTotaldue(), curr, centCurr) + " Only");
            }

            debitDocRepository.save(invoice);
            if(message.getRecordPaymentDTO() != null) {
                try {
                    RecordPaymentPojo recordPaymentDTO = message.getRecordPaymentDTO();
                    if(recordPaymentDTO.getChequedatestr() != null) {
                        recordPaymentDTO.setChequedate(LocalDate.parse(recordPaymentDTO.getChequedatestr()));
                    }
                    if(recordPaymentDTO.getPaymentdatestr() != null) {
                        recordPaymentDTO.setPaymentdate(LocalDate.parse(recordPaymentDTO.getPaymentdatestr()));
                    }
                    recordPaymentDTO.setInvoiceId(Collections.singletonList(invoice.getId()));
                    recordPaymentDTO.setPaytype("invoice");
                    List<PaymentListPojo> paymentListPojos =
                            recordPaymentDTO.getPaymentListPojos().stream().peek(paymentListPojo -> paymentListPojo.setInvoiceId(invoice.getId())).collect(Collectors.toList());
                    recordPaymentDTO.setPaymentListPojos(paymentListPojos);
                    message.setRecordPaymentDTO(recordPaymentDTO);
                    if(message.getData().get("currentUserLoggedInId")!=null)
                    {
                        StaffUserRepository staffUserRepository=SpringContext.getBean(StaffUserRepository.class);
                        StaffUser staffUser=staffUserRepository.findById(Integer.parseInt(message.getData().get("currentUserLoggedInId").toString())).orElse(null);
                        if(staffUser!=null && staffUser.getPartnerid()==1)
                        {
                            CreditDocService creditDocService=SpringContext.getBean(CreditDocService.class);
                            CreditDocument creditDocument =  creditDocService.save(message.getRecordPaymentDTO(), false, false, false,mvnoId,invoice.getLcoId(), Collections.singletonList(invoice.getBuId()),isLco,invoice.getCreatedById(),invoice.getCreatedByName());
                            if(recordPaymentDTO.getIsAdjusted() != null && recordPaymentDTO.getIsAdjusted()){
                                creditDocService.addPaymentInCustomerLedger(debitDocument.getCustomer() , creditDocument);
                                creditDocService.adjustCreditdebitDoc(debitDocument,creditDocument);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while add payment: "+e.getMessage());
                }
            }
        }
    }



    public void updateBillRunData(DebitDocument debitDocument, Integer billRunId) {
        try {
            BillRunRepository billRunRepository=SpringContext.getBean(BillRunRepository.class);
            Optional<BillRun> billRun = billRunRepository.findById(billRunId);
            if (billRun.isPresent()) {
                billRun.get().setStatus("Generated");
                billRun.get().setAmount(billRun.get().getAmount() + debitDocument.getTotalamount());
                billRun.get().setSuccessCount(billRun.get().getSuccessCount()+1);
                billRunRepository.save(billRun.get());
            }
        }catch (Exception ex) {
            logger.error("error while update billRunData: "+ex.getMessage());
        }
    }
}
