package com.savbill.revenuemanagement.rabbitmq;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanService;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.invoice.PaymentListPojo;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.repository.BillRun.BillRunRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.Inventory.InventoryInvoiceService;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.ChargeInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.savbill.revenuemanagement.core.service.common.InvoiceUtil.convertToAmount;
import static com.savbill.revenuemanagement.core.service.common.InvoiceUtil.getSubunitName;

@Component
public class MessageReceiverWithThread {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiverWithThread.class);

    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private ChargeInvoiceService chargeInvoiceService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private InventoryInvoiceService inventoryInvoiceService;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private BillRunRepository billRunRepository;

    @Autowired
    private InvoiceUtil invoiceUtil;
    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;
    @Autowired
    Tracer tracer;

//    @RabbitListener(queues = RabbitMqConstants.QUEUE_BILLING_INVOICE, concurrency = "1000")
    public void receiveBillingInvoiceMessage(CustomerBillingMessage message) {
        logger.info("*********message start: " + LocalDateTime.now() + " in milli: " + new Date().getTime() + " message: "+message);
        // Create a copy of the message for each thread
        CustomerBillingMessage messageCopy = createCopy(message);
        TraceContext traceContext =tracer.currentSpan().context();
        messageCopy.setTraceContext(traceContext);

        // Start a new thread for processing with the message copy
        Thread thread = new Thread(() -> processMessage(messageCopy,null));
        thread.start();
    }


   /* @RabbitListener(queues = RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE)
    public void receiveMessageCreditDocFromAPIGW(CreditDocMessage message) {
        System.out.println("Message : " + message);
        try {
            creditDocService.save(message);
            Customers customers=customersRepository.findById(message.getCustomer()).orElse(null);
            customers.setWalletbalance(message.getWalletBalance());
            customersRepository.save(customers);

            System.out.println("success..!!");
        } catch (Exception e) {
            logger.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
        }

    }*/

    public void receiveBillingInvoiceMessageForPostpaid(CustomerBillingMessage message) {
        logger.info("*********message start: " + LocalDateTime.now() + " in milli: " + new Date().getTime() + " message: "+message);
        // Create a copy of the message for each thread
        CustomerBillingMessage messageCopy = createCopy(message);

        // Start a new thread for processing with the message copy
        Thread thread = new Thread(() -> processMessage(messageCopy,null));
        thread.start();
    }

    private CustomerBillingMessage createCopy(CustomerBillingMessage message) {
        // Example copy constructor usage:
        return new CustomerBillingMessage(message);
    }

    public void processMessage(CustomerBillingMessage message,Customers customers) {
        DebitDocument debitDocument = null;
        TrialDebitDocument trialDebitDocument=null;
        Map<String, Object> data = message.getData();
        Integer mvnoId = null;
        Integer lcoId = null;
        Boolean isLco = false;
        if(data.containsKey(CustomerBillingMessage.MVNOID))
            mvnoId = Integer.valueOf(data.get(CustomerBillingMessage.MVNOID).toString());
        if(data.containsKey(CustomerBillingMessage.ISLCO))
            isLco = Boolean.valueOf(data.get(CustomerBillingMessage.ISLCO).toString());
        try {
            // Process the message in this thread without sharing data
            if(message.getType() != null && !message.getType().equals("null") && !message.getType().isEmpty())
           // if(message.getType()!=null)
            {
                Boolean iscaf = false;

                if(message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CUSTOMER_CHARGE)) {
                    if (data.get(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE)!=null) {
                        iscaf = data.get(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE).equals("true");
                    }
                    if (iscaf!=null && iscaf){
                        trialDebitDocument = chargeInvoiceService.createCafCustomerChargeInvoice(message);
                    }else {
                        debitDocument = chargeInvoiceService.createCustomerChargeInvoice(message);
                    }
                } else if(message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.INVENTORY)) {

                    Long custId =((Number) data.get(CustomerBillingMessage.CUST_ID)).longValue();
                    String status = customersRepository.findStatusById(custId.intValue());
                    iscaf = message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                        if (status.equalsIgnoreCase("NewActivation")){
                            iscaf = true;
                        }
                        if(iscaf!=null && iscaf) {
                            trialDebitDocument = inventoryInvoiceService.customerInventoryInvoiceForCaf(message);//CAF creation
                        }else {
                            debitDocument = inventoryInvoiceService.createCustomerInventoryInvoice(message);
                        }

                } else {
                    //Create customer and renew
                    Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
                    String status = message.getCustomerStatus();
                    if(status==null)
                        status=customersRepository.findStatusById(custId);

                    iscaf = message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                    if (status.equalsIgnoreCase("NewActivation")&&  message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)){
                        iscaf = true;
                    }
                    if(iscaf!=null && iscaf) {
                        //CAF creation
                        trialDebitDocument = prepaidInvoiceService.createPrepaidInvoiceCaf(message);
                    } else if (message.getCustType()!=null && message.getCustType().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                        debitDocument=postpaidInvoiceService.createPostPaidInvoice(message,customers);
                    }else {
                        debitDocument = prepaidInvoiceService.createPrepaidInvoice(message,customers);
                    }
                }
            } else {
                //Create customer and renew
                if (message.getCustType()!=null && message.getCustType().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    debitDocument=postpaidInvoiceService.createPostPaidInvoice(message,customers);
                }else {
                    debitDocument = prepaidInvoiceService.createPrepaidInvoice(message,customers);
                }
            }
        } catch (Exception ex) {
            logger.error("Error in processMessage: "+ex.getMessage());
        }

        if(trialDebitDocument!=null)
        {
            Optional<TrialDebitDocument> trialDebitDocument1=trialDebitDocRepository.findById(trialDebitDocument.getId());
            if(trialDebitDocument1.isPresent())
            {
                String xml = prepaidInvoiceService.setInvoiceXml(trialDebitDocument1.get(),trialDebitDocument1.get().getTrialDebitDocumentDetails());
                trialDebitDocument1.get().setDuedate(trialDebitDocument1.get().getEndate());
                trialDebitDocument1.get().setDocument(xml);
                trialDebitDocRepository.save(trialDebitDocument1.get());
            }
            autoRenewOrAddonPlanService.autoAdjustPaymentAgainstInvoice(message,null,trialDebitDocument1.get());
        }

        if(debitDocument != null) {
            DebitDocument invoice = debitDocument;
            if(invoice!=null) {

                autoRenewOrAddonPlanService.autoAdjustPaymentAgainstInvoice(message,debitDocument,null);
                if(message.getRecordPaymentDTO() != null && message.getRecordPaymentDTO().getPaymentListPojos()!=null) {
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
                        List<PaymentListPojo> paymentListPojos =null;
                        if(recordPaymentDTO.getPaymentListPojos()!=null)
                            paymentListPojos=recordPaymentDTO.getPaymentListPojos().stream().peek(paymentListPojo -> paymentListPojo.setInvoiceId(invoice.getId())).collect(Collectors.toList());
                        recordPaymentDTO.setPaymentListPojos(paymentListPojos);
                        message.setRecordPaymentDTO(recordPaymentDTO);
                        if(message.getData().get("currentUserLoggedInId")!=null)
                        {
                            StaffUser staffUser=staffUserRepository.findById(Integer.parseInt(message.getData().get("currentUserLoggedInId").toString())).orElse(null);
                            if(staffUser!=null && staffUser.getPartnerid()==1)
                            {
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
    }

    public void updateBillRunData(DebitDocument debitDocument, Integer billRunId) {
        try {
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

    public void processMessagePostpaid(CustomerBillingMessage message) {
        DebitDocument debitDocument = null;
        TrialDebitDocument trialDebitDocument=null;
        Map<String, Object> data = message.getData();
        Integer mvnoId = null;
        Integer lcoId = null;
        Boolean isLco = false;
        if(data.containsKey(CustomerBillingMessage.MVNOID))
            mvnoId = Integer.valueOf(data.get(CustomerBillingMessage.MVNOID).toString());
        if(data.containsKey(CustomerBillingMessage.ISLCO))
            isLco = Boolean.valueOf(data.get(CustomerBillingMessage.ISLCO).toString());
        try {
            // Process the message in this thread without sharing data
            if(message.getType() != null && !message.getType().equals("null") && !message.getType().isEmpty())
            // if(message.getType()!=null)
            {
                Boolean iscaf = false;

                if(message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CUSTOMER_CHARGE)) {
                    if (data.get(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE)!=null) {
                        iscaf = data.get(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE).equals("true");
                    }
                    if (iscaf!=null && iscaf){
                        trialDebitDocument = chargeInvoiceService.createCafCustomerChargeInvoice(message);
                    }else {
                        debitDocument = chargeInvoiceService.createCustomerChargeInvoice(message);
                    }
                } else if(message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.INVENTORY)) {

                    Long custId = (Long) data.get(CustomerBillingMessage.CUST_ID);
                    String status = customersRepository.findStatusById(custId.intValue());
                    iscaf = message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                    if (status.equalsIgnoreCase("NewActivation")){
                        iscaf = true;
                    }
                    if(iscaf!=null && iscaf) {
                        trialDebitDocument = inventoryInvoiceService.customerInventoryInvoiceForCaf(message);//CAF creation
                    }else {
                        debitDocument = inventoryInvoiceService.createCustomerInventoryInvoice(message);
                    }

                } else {
                    //Create customer and renew
                    Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
                    String status = customersRepository.findStatusById(custId);
                    iscaf = message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                    if (status.equalsIgnoreCase("NewActivation")&&  message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)){
                        iscaf = true;
                    }
                    if(iscaf!=null && iscaf) {
                        //CAF creation
                        trialDebitDocument = prepaidInvoiceService.createPrepaidInvoiceCaf(message);
                    }else {
                        debitDocument = prepaidInvoiceService.createPrepaidInvoice(message,null);
                    }
                }
            } else {
                //Create customer and renew
                debitDocument = prepaidInvoiceService.createPrepaidInvoice(message,null);
            }
        } catch (Exception ex) {
            logger.error("Error in processMessage: "+ex.getMessage());
        }
        if(debitDocument != null){
            String xml = prepaidInvoiceService.setInvoiceXml(debitDocument);
            Optional<DebitDocument> invoice = debitDocRepository.findById(debitDocument.getId());
            if(invoice.isPresent()) {
                invoice.get().setDocument(xml);
                if(debitDocument.getCustomer()!=null && debitDocument.getCustomer().getCusttype().equalsIgnoreCase("Postpaid"))
                {
                    debitDocument.setEndate(debitDocument.getEndate().minusDays(1));
                }
                if(message.getData().get(CustomerBillingMessage.BILL_RUN_ID) != null) {
                    updateBillRunData(invoice.get(), Integer.valueOf(message.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
                    invoice.get().setBillrunid(Integer.valueOf(message.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
                }
                if(invoice.get().getCustomer().getCurrency() != null){
                    String centCurrDynamic = getSubunitName(invoice.get().getCustomer().getCurrency());
                    invoice.get().setTotalamountinwords(convertToAmount((invoice.get().getTotalamount() * 100) / 100 , invoice.get().getCustomer().getCurrency(), centCurrDynamic) + " Only");
                    invoice.get().setTotaldueinwords(convertToAmount(invoice.get().getTotaldue(), invoice.get().getCustomer().getCurrency(), centCurrDynamic) + " Only");
                } else {
                    invoice.get().setTotalamountinwords(convertToAmount((invoice.get().getTotalamount() * 100) / 100 , curr, centCurr) + " Only");
                    invoice.get().setTotaldueinwords(convertToAmount(invoice.get().getTotaldue(), curr, centCurr) + " Only");
                }
                debitDocRepository.save(invoice.get());
                if(message.getRecordPaymentDTO() != null) {
                    try {
                        RecordPaymentPojo recordPaymentDTO = message.getRecordPaymentDTO();
                        if(recordPaymentDTO.getChequedatestr() != null) {
                            recordPaymentDTO.setChequedate(LocalDate.parse(recordPaymentDTO.getChequedatestr()));
                        }
                        if(recordPaymentDTO.getPaymentdatestr() != null) {
                            recordPaymentDTO.setPaymentdate(LocalDate.parse(recordPaymentDTO.getPaymentdatestr()));
                        }
                        recordPaymentDTO.setInvoiceId(Collections.singletonList(invoice.get().getId()));
                        recordPaymentDTO.setPaytype("invoice");
                        List<PaymentListPojo> paymentListPojos =
                                recordPaymentDTO.getPaymentListPojos().stream().peek(paymentListPojo -> paymentListPojo.setInvoiceId(invoice.get().getId())).collect(Collectors.toList());
                        recordPaymentDTO.setPaymentListPojos(paymentListPojos);
                        message.setRecordPaymentDTO(recordPaymentDTO);
                        CreditDocument creditDocument =  creditDocService.save(message.getRecordPaymentDTO(), false, false, false,mvnoId,invoice.get().getLcoId(), Collections.singletonList(invoice.get().getBuId()),isLco,invoice.get().getCreatedById(),invoice.get().getCreatedByName());
                        if(recordPaymentDTO.getIsAdjusted() != null && recordPaymentDTO.getIsAdjusted()){
                            creditDocService.addPaymentInCustomerLedger(debitDocument.getCustomer() , creditDocument);
                            creditDocService.adjustCreditdebitDoc(debitDocument,creditDocument);
                        }
                    } catch (Exception e) {
                        logger.error("Error while add payment: "+e.getMessage());
                    }
                }

            }
        }
    }


    public void receiveBillingInvoiceMessageForManual(CustomerBillingMessage message) {
        //logger.info("********* Invoice message start: " + LocalDateTime.now() + " in milli: " + new Date().getTime() + " message: "+message);
        // Create a copy of the message for each thread
        CustomerBillingMessage messageCopy = createCopy(message);
        messageCopy.setTrailPlanFromToday(message.isTrailPlanFromToday());
        messageCopy.setTrailPlanFromTrailDay(message.isTrailPlanFromTrailDay());
        messageCopy.setCafCustomerApprove(message.isCafCustomerApprove());
        messageCopy.setPlanValidityChangePlan(message.isPlanValidityChangePlan());
        if(!message.isTracerIdNotRequired()) {
            TraceContext traceContext = tracer.currentSpan().context();
            messageCopy.setTraceContext(traceContext);
        }
        processMessage(messageCopy,null);
        // Start a new thread for processing with the message copy
        //Thread thread = new Thread(() -> processMessage(messageCopy,null));
        //thread.start();
        //logger.info("********* Invoice message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
    }

    public void receiveBillingInvoiceMessageScheduler(CustomerBillingMessage message,Customers customers) {
        CustomerBillingMessage messageCopy = createCopy(message);
        messageCopy.setTraceContext(message.getTraceContext());
        processMessage(messageCopy,customers);
    }
}
