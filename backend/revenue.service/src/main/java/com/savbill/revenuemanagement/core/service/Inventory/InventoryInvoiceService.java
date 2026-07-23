package com.savbill.revenuemanagement.core.service.Inventory;

import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.LogConstants;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocumentDetailRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.service.common.CustomerInventoryUtil;
import com.savbill.revenuemanagement.core.service.prepaid.DbrService;
import com.savbill.revenuemanagement.core.service.prepaid.PartnerCommissionService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceCharges;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;

import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryInvoiceService {



    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private CustomerInventoryUtil customerInventoryUtil;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private TaxService taxService;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private TrialDebitDocumentDetailRepository trialDebitDocumentDetailRepository;

    //@Autowired
    //MessageSender messageSender;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    private static final Logger logger = Logger.getLogger(InventoryInvoiceService.class);


    public DebitDocument createCustomerInventoryInvoice(CustomerBillingMessage customerBillingMessage) {
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            MDC.put("type", "Create");
            TraceContext traceContext =customerBillingMessage.getTraceContext();
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
                return null;
            }
            if(!data.containsKey("customerInventoryMappId")) {
                logger.error("customer Inventory mapping id is empty!");
                return null;
            }
            List<Long> custInvIds = (List<Long>) data.get("customerInventoryMappId");
            // Long custId = Long.valueOf(data.get("custId").toString());
            Long custId = Long.valueOf(data.get("custId").toString());
            Integer staffId=null;
            if(data.get("currentUserLoggedInId")!=null)
                staffId = (Integer) data.get("currentUserLoggedInId");

            List<CustomerInventoryMapping> inventoryMappings = customerInventoryMappingRepo.findAllByIdInAndCustomerId(custInvIds, custId);
            Customers customers = customersRepository.findById(inventoryMappings.get(0).getCustomerId().intValue()).get();
            InvoiceDetails invoiceDetails = customerInventoryUtil.prepareInvoiceInventoryDetail(customers, inventoryMappings);
            DebitDocument debitDocument = invoiceDetails.getDebitDocument();
            if(debitDocument.getTotalamount() <= 0) {
                logger.error("Invoice can not be generated due to 0 ammount");
                return null;
            }
            debitDocument.setNextStaff(staffId);
            debitDocument.setDuedate(debitDocument.getStartdate());
            debitDocument = debitDocRepository.save(debitDocument);
            List<DebitDocDetails> debitDocDetailsList = invoiceDetails.getDebitDocDetails();
            DebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            debitDocDetailRepository.saveAll(debitDocDetailsList);

            //Add ledger details
            prepaidInvoiceService.addCustomerLedger(debitDocument, customers,null,null,null,null);
            //if has onetime charge then add in dbr
            finalDebitDocument.setDebitDocDetailsList(debitDocDetailsList);
            dbrService.addDbrForCustomerInventoryCharge(customers.getId(), finalDebitDocument);
//            dbrService.addOneTimeEntryForPrepaidIntoChargeDBR(custChargeDetailsList, customers, debitDocument, 1L, null);
            //Update CPR with flag and invoice id
            updateCustInventoryDetailAfterInvoiceCreated(inventoryMappings, Long.valueOf(finalDebitDocument.getId()));
            try {
                partnerCommissionService.inventoryPayment(inventoryMappings, debitDocument.getTotalamount(), customers, staffId, debitDocument);
            }catch (Exception e){logger.error("Error while payment for Inventory Invoice : " + e.getStackTrace());}
            List<DebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            int i=0;
            Long docDetailId=null;
            for (CustomerInventoryMapping inventoryMapping : inventoryMappings) {
                if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty())
                    docDetailId = debitDocDetailsList.get(i).getDebitdocdetailid().longValue();
                DebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge(finalDebitDocument, inventoryMapping.getChargeId().intValue(),inventoryMapping.getDiscount(),docDetailId, inventoryMapping.getBillTo());
                if (debitDocumentTAXRel != null) {
                    debitDocumentTAXRel.setPlanName("");
                    debitDocumentTAXRels.add(debitDocumentTAXRel);
                }
            }
            logger.info("*********message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
//            Customers refCustomer = custPlanMapppings.get(0).getCustomer();
            String isCaf="false";
            if(customers.getCafno()!=null){
                isCaf="true";
            }
            boolean isOrgCust=false;
            if(customers.getId()==1){
                isOrgCust=true;
            }
            customerBillingMessage.setType(Constants.INVOICE_TYPE.CANCEL_REGENERATE);
            prepaidInvoiceService.createCNforChangePlanAndCancelAndRegenrate(customerBillingMessage, debitDocument, null,inventoryMappings.stream().map(CustomerInventoryMapping::getId).collect(Collectors.toList()),null);


            PrepaidInvoiceCharges prepaidInvoiceCharges=new PrepaidInvoiceCharges(customers.getId(),customers.getUsername(),customers.getCustomerType(),debitDocument.getTotalamount(),debitDocument.getId().longValue(),customers.getUsername(),isOrgCust,debitDocument.getTotalamount(),customers.getCreatedById(),null,null,"null","false",isCaf,inventoryMappings.get(0).getId(),debitDocument,customers.getWalletbalance(),debitDocument.getPaymentStatus(),debitDocument.getBillrunid(),debitDocument.getCreatedByName(),new ArrayList<>(),debitDocument.getAdjustedAmount(),debitDocument.getBillrunstatus(),false,debitDocument.getIsDirectChargeInvoice(),null,null,null,null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges,PrepaidInvoiceCharges.class.getSimpleName()));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, "+"Successfully Invoice Created for Customer id :" +  Long.valueOf(data.get("custId").toString()) + LogConstants.REQUEST_BY + staffId +  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        }catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM+ " Customer management Service, "+"Error During Invoice Generation for Customer id : " +   Long.valueOf(data.get("custId").toString()) +   LogConstants.REQUEST_BY + data.get("currentUserLoggedInId") +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    public void updateCustInventoryDetailAfterInvoiceCreated(List<CustomerInventoryMapping> inventoryMappings, Long debitDocId) {
        if(!CollectionUtils.isEmpty(inventoryMappings)) {
            inventoryMappings.stream().peek(invMapp -> {
                invMapp.setIsInvoiceCreated(true);
            });
            customerInventoryMappingRepo.saveAll(inventoryMappings);
        }
    }

    public TrialDebitDocument customerInventoryInvoiceForCaf(CustomerBillingMessage customerBillingMessage) {
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            MDC.put("type", "Create");
            TraceContext traceContext =customerBillingMessage.getTraceContext();
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
                return null;
            }
            if(!data.containsKey("customerInventoryMappId")) {
                logger.error("customer Inventory mapping id is empty!");
                return null;
            }
            List<Long> custInvIds = (List<Long>) data.get("customerInventoryMappId");
            // Long custId = Long.valueOf(data.get("custId").toString());
            Long custId = Long.valueOf(data.get("custId").toString());
            Integer staffId=null;
            if(data.get("currentUserLoggedInId")!=null)
                staffId = (Integer) data.get("currentUserLoggedInId");

            List<CustomerInventoryMapping> inventoryMappings = customerInventoryMappingRepo.findAllByIdInAndCustomerId(custInvIds, custId);
            inventoryMappings=inventoryMappings.stream().filter(x->x.getIsDeleted()!=null && x.getIsDeleted().equals(false)).collect(Collectors.toList());
            Customers customers = customersRepository.findById(inventoryMappings.get(0).getCustomerId().intValue()).get();
            TrialInvoiceDetails invoiceDetails = customerInventoryUtil.prepareCafInvoiceInventoryDetail(customers, inventoryMappings);
            TrialDebitDocument debitDocument = invoiceDetails.getTrialDebitDocument();
            if(debitDocument.getTotalamount() <= 0) {
                logger.error("Invoice can not be generated due to 0 ammount");
                return null;
            }
            debitDocument.setTrialDebitDocumentDetails(new ArrayList<>());
            debitDocument.getCustomer().setPlanMappingList(null);
            debitDocument = trialDebitDocRepository.save(debitDocument);
            List<TrialDebitDocumentDetail> debitDocDetailsList = invoiceDetails.getTrialDebitDocDetails();
            TrialDebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            trialDebitDocumentDetailRepository.saveAll(debitDocDetailsList);

            List<TrialDebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            int i=0;
            Long docDetailId=null;
            for (CustomerInventoryMapping inventoryMapping : inventoryMappings) {
                if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty())
                    docDetailId = debitDocDetailsList.get(i).getDebitdocdetailid().longValue();
                TrialDebitDocumentTAXRel debitDocumentTAXRel = taxService.setTrialTaxAmountFromCharge(finalDebitDocument, inventoryMapping.getChargeId().intValue(),inventoryMapping.getDiscount(),docDetailId, inventoryMapping.getBillTo());
                if (debitDocumentTAXRel != null) {
                    debitDocumentTAXRel.setPlanName("");
                    debitDocumentTAXRels.add(debitDocumentTAXRel);
                }
            }
            logger.info("*********message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());

            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, "+"Successfully Invoice Created for Customer id :" +  Long.valueOf(data.get("custId").toString()) + LogConstants.REQUEST_BY + staffId +  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        }catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM+ " Customer management Service, "+"Error During Invoice Generation for Customer id : " +   Long.valueOf(data.get("custId").toString()) +   LogConstants.REQUEST_BY + data.get("currentUserLoggedInId") +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }
}
