package com.savbill.revenuemanagement.core.service.prepaid;

import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.customers.CustChargeDetails;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.repository.customer.CustChargeDetailsRepository;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.service.common.ChargeInvoiceUtil;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChargeInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(ChargeInvoiceService.class);

    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    private ChargeInvoiceUtil chargeInvoiceUtil;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private TrialDebitDocumentDetailRepository trialDebitDocumentDetailRepository;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private TaxService taxService;

    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

//    @Autowired
//    private MessageSender messageSender;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private TrialDebitDocumentTAXRelRepository trialDebitDocumentTAXRelRepository;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    /**
     Create customer invoice
     * @param customerBillingMessage
     */
    //@Transactional
    public DebitDocument createCustomerChargeInvoice(CustomerBillingMessage customerBillingMessage) {
        try {
            Map<String, Object> data = customerBillingMessage.getData();

            Integer staffId = null;
            if (data.get("currentUserLoggedInId") != null)
                staffId = (Integer) data.get("currentUserLoggedInId");

            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
                return null;
            }

            DebitDocument oldDebitDocument=null;
            if(data.containsKey("oldDebitDocId") && data.get("oldDebitDocId") != null)
            {
                Optional<DebitDocument> optionalDocument=debitDocRepository.findById(Integer.parseInt(data.get("oldDebitDocId").toString()));
                if(optionalDocument.isPresent())
                    oldDebitDocument=optionalDocument.get();
            }
           else if(customerBillingMessage.getCustChargeIds()==null || customerBillingMessage.getCustChargeIds().size()==0) {
                    logger.error("customer charge mapping id is empty!");
                    return null;
           }

            List<Integer> custChargeId = customerBillingMessage.getCustChargeIds();
            if(custChargeId==null && oldDebitDocument!=null)
            {
                custChargeId=new ArrayList<>();
                CustChargeDetails details=custChargeDetailsRepository.findAllByDebitdocid(oldDebitDocument.getId().longValue());
                custChargeId.add(details.getId());
            }
            List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(custChargeId);
            if(customerBillingMessage!=null && customerBillingMessage.isMvnoCustomer()) {
                if(customerBillingMessage.getTotalPrice()!=null && customerBillingMessage.getTotalPrice()>0.0 && !CollectionUtils.isEmpty(custChargeDetailsList)) {
                    Integer customerId = custChargeDetailsList.get(0).getCustomer().getId();
                    Double ispCommissionPercentage=mvnoRepository.findIspCommissionPercentageByMvnoId(customerId);
                    if(ispCommissionPercentage!=null) {
                        Double commissionPrice=customerBillingMessage.getTotalPrice()*ispCommissionPercentage/100.0d;
                        custChargeDetailsList.get(0).setPrice(custChargeDetailsList.get(0).getPrice()-commissionPrice);
                    }
                    else
                        custChargeDetailsList.get(0).setPrice(custChargeDetailsList.get(0).getPrice()-customerBillingMessage.getTotalPrice());
                    custChargeDetailsList.get(0).setActualprice(custChargeDetailsList.get(0).getActualprice()-customerBillingMessage.getTotalPrice());
                    custChargeDetailsRepository.save(custChargeDetailsList.get(0));
                }
            }
            if(CollectionUtils.isEmpty(custChargeDetailsList)) {
                logger.error("customer charge mapping not available for given id: "+custChargeId);
                return null;
            }
            Customers customers = custChargeDetailsList.get(0).getCustomer();

            //Process for invoice creation
            InvoiceDetails invoiceDetails = chargeInvoiceUtil.prepareInvoiceChargeDetail(customers, custChargeDetailsList,customerBillingMessage);
            DebitDocument debitDocument = invoiceDetails.getDebitDocument();
            debitDocument.setIsDirectChargeInvoice(true);
            if(debitDocument.getTotalamount() <= 0) {
                logger.error("Invoice can not be generated due to 0 ammount");
                return null;
            }
            if(customerBillingMessage.isMvnoCustomer()) {
                if(!CollectionUtils.isEmpty(customerBillingMessage.getDebitDocDetailIds())) {
                    LocalDateTime startDate = customerBillingMessage.getIspFromDate().atStartOfDay();
                    debitDocument.setStartdate(startDate);
                    debitDocument.setEndate(customerBillingMessage.getIspToDate().atStartOfDay());
                    Integer custId = debitDocument.getCustomer().getId();
                    Integer dueDays = mvnoRepository.findDuedaysByMvnoId(custId);
                    LocalDateTime dueDate = startDate.plusDays(dueDays);
                    if (dueDate.isBefore(LocalDateTime.now()) || dueDate.isEqual(LocalDateTime.now())){
                        dueDate = LocalDateTime.now().plusDays(dueDays);
                    }
                    debitDocument.setDuedate(dueDate);
                }
            }
            debitDocument = debitDocRepository.save(debitDocument);
            List<DebitDocDetails> debitDocDetailsList = invoiceDetails.getDebitDocDetails();
            DebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            debitDocDetailRepository.saveAll(debitDocDetailsList);

            //Add ledger details
            prepaidInvoiceService.addCustomerLedger(debitDocument, customers,null,null,null,null);
            //For cancel and regenrate and Change plan
            if (data.containsKey("oldDebitDocId") && data.get("oldDebitDocId") != null) {
                customerBillingMessage.setType(Constants.INVOICE_TYPE.CANCEL_REGENERATE);
            }
            if (customerBillingMessage.isMvnoCustomer() && customerBillingMessage.getType()!=null && customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                customerLedgerDtlsRepository.markAsDeletedByCustomerIdAndDebitDocId(finalDebitDocument.getCustomer().getId(),oldDebitDocument.getId());
            }else {
                prepaidInvoiceService.createCNforChangePlanAndCancelAndRegenrate(customerBillingMessage, debitDocument, null, null, null);
            }
            //if has onetime charge then add in dbr
            dbrService.addOneTimeEntryForPrepaidIntoDBR(custChargeDetailsList, customers, debitDocument, 1, null);
            try {
                partnerCommissionService.paymentAdjustmentAgainstDirectChargeInvoice(debitDocument,customers,staffId);
            }
            catch (Exception e){
                logger.error("Customer Direct-Charge Partner Payment Error : "+e.getStackTrace());
            }
           //Update CPR with flag and invoice id
            updateCustChargeDetailAfterInvoiceCreated(custChargeDetailsList, Long.valueOf(finalDebitDocument.getId()));

            List<DebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            Integer i=0;
            Long docDetailId=null;
            for (CustChargeDetails custChargeDetails : custChargeDetailsList) {
                if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty())
                    docDetailId=debitDocDetailsList.get(i).getDebitdocdetailid().longValue();
                debitDocumentTAXRels= taxService.setTaxAmountFromCharge2(finalDebitDocument, custChargeDetails.getChargeid(), custChargeDetails.getDiscount(), docDetailId, custChargeDetails.getBillTo(),debitDocumentTAXRels);
                //DebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge(finalDebitDocument, custChargeDetails.getChargeid(), custChargeDetails.getDiscount(), docDetailId, custChargeDetails.getBillTo());
                //debitDocumentTAXRels.add(debitDocumentTAXRel);
            }
            if(!CollectionUtils.isEmpty(debitDocumentTAXRels))
                debitDocument.setDebitDocumentTAXRels(debitDocumentTAXRels);
            boolean isOrgCust=false;
            if(customers.getId()==1){
                isOrgCust=true;
            }
            List<Long> custServiceIds = new ArrayList<>();
            String isCaf="false";
            List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPair = new ArrayList<>();
            List<Integer> chargeIds = custChargeDetailsList.stream().map(CustChargeDetails::getId).collect(Collectors.toList());
            if(customerBillingMessage.isMvnoCustomer()) {
                debitDocument.setUpdateDebitDpcDetailsIds(customerBillingMessage.getDebitDocDetailIds());
            }
            PrepaidInvoiceCharges prepaidInvoiceCharges=new PrepaidInvoiceCharges( customers.getId(),customers.getUsername(),customers.getCustomerType(),debitDocument.getTotalamount(),debitDocument.getId().longValue(),customers.getUsername(),isOrgCust,debitDocument.getTotalamount(),customers.getCreatedById(),null,custServiceIds,"null","false",isCaf,0L,debitDocument,customers.getWalletbalance(),debitDocument.getPaymentStatus(),debitDocument.getBillrunid(),debitDocument.getCreatedByName(),CustPackAndDebitDocIdPair,debitDocument.getAdjustedAmount(),debitDocument.getBillrunstatus(),false, debitDocument.getIsDirectChargeInvoice(),null,null,null,null);
            prepaidInvoiceCharges.setChargeIds(chargeIds);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges,PrepaidInvoiceCharges.class.getSimpleName()));
            logger.info("*********message create Prepaid Invoice end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
            logger.info("*********message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
            if(!CollectionUtils.isEmpty(customerBillingMessage.getDebitDocDetailIds())) {
                updateDebitDocDetailsForMvnoInoivce(customerBillingMessage.getDebitDocDetailIds(), debitDocument.getId());
            }
            return debitDocument;
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while generate exception at create invoice for customer charge: "+ex.getMessage());
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDebitDocDetailsForMvnoInoivce(List<Integer> debitDocDetailsIds, Integer mvnoDocId) {
        try {
            debitDocDetailRepository.updateMvNodeBitDocumentId(mvnoDocId, debitDocDetailsIds);
        } catch (Exception ex) {
            logger.error("Exception to update mvnoDocId in customer invoices: "+ex.getMessage());
        }
    }

    /**
     Update CPR after invoice created
     * @Author Yogesh
     * @param custChargeDetails
     * @param debitDocId
     *
     */
    public void updateCustChargeDetailAfterInvoiceCreated(List<CustChargeDetails> custChargeDetails, Long debitDocId) {
        if(!CollectionUtils.isEmpty(custChargeDetails)) {
            custChargeDetails.forEach(custChargeDet -> {
                custChargeDet.setIsUsed(true);
                custChargeDet.setDebitdocid(debitDocId);
            });
            custChargeDetailsRepository.saveAll(custChargeDetails);

        }
    }

    public void updateCustChargeDetailAfterInvoiceCreatedCaf(List<CustChargeDetails> custChargeDetails, Long debitDocId) {
        if(!CollectionUtils.isEmpty(custChargeDetails)) {
            custChargeDetails.forEach(custChargeDet -> {
                custChargeDet.setIsUsed(false);
                custChargeDet.setDebitdocid(debitDocId);
            });
            custChargeDetailsRepository.saveAll(custChargeDetails);

        }
    }

    /**
     invoice creation for caf direct charge
     * @Author Vikas
     *
     */
    public TrialDebitDocument createCafCustomerChargeInvoice(CustomerBillingMessage customerBillingMessage) {
        try {
            Map<String, Object> data = customerBillingMessage.getData();
            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
                return null;
            }
            if(customerBillingMessage.getCustChargeIds()==null && customerBillingMessage.getCustChargeIds().size()==0) {
                logger.error("customer charge mapping id is empty!");
                return null;
            }

            List<Integer> custChargeId = customerBillingMessage.getCustChargeIds();


            List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(custChargeId);
            if(CollectionUtils.isEmpty(custChargeDetailsList)) {
                logger.error("customer charge mapping not available for given id: "+custChargeId);
                return null;
            }
            Customers customers = custChargeDetailsList.get(0).getCustomer();

            //Process for invoice creation
            TrialInvoiceDetails invoiceDetails = chargeInvoiceUtil.prepareInvoiceChargeDetailCaf(customers, custChargeDetailsList);
            TrialDebitDocument debitDocument = invoiceDetails.getTrialDebitDocument();
            debitDocument.setPaymentStatus(Constants.INVOICE_PAYMENT_STATUS.UNPAID.status());
            debitDocument.setTrialDebitDocumentDetails(null);
            debitDocument.setCreatedByName(customerBillingMessage.getCreatedByName());
            debitDocument = trialDebitDocRepository.save(debitDocument);
            List<TrialDebitDocumentDetail> debitDocDetailsList = invoiceDetails.getTrialDebitDocDetails();
            TrialDebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            trialDebitDocumentDetailRepository.saveAll(debitDocDetailsList);

            //For cancel and regenrate and Change plan
            if (data.containsKey("isCancelRegenerate") && data.get("isCancelRegenerate") != null) {
                customerBillingMessage.setType(Constants.INVOICE_TYPE.CHANGE_PLAN);
                String isCancelRegenerate =  data.get("isCancelRegenerate").toString();
                if(Boolean.parseBoolean(isCancelRegenerate))
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.CANCEL_REGENERATE);
            }
            //Update CPR with flag and invoice id
            updateCustChargeDetailAfterInvoiceCreatedCaf(custChargeDetailsList, Long.valueOf(finalDebitDocument.getId()));
//            List<DebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
//            custChargeDetailsList.forEach(custChargeDetails -> {
//                DebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge(finalDebitDocument, custChargeDetails.getChargeid());
//                if (debitDocumentTAXRel != null) {
//                    debitDocumentTAXRel.setPlanName("");
//                    debitDocumentTAXRels.add(debitDocumentTAXRel);
//                }
//            });
            List<TrialDebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            Integer i=0;
            Long docDetailId=null;
            finalDebitDocument.setTrialDebitDocumentDetails(debitDocDetailsList);
            for (CustChargeDetails custChargeDetails : custChargeDetailsList) {
                if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty())
                    docDetailId=debitDocDetailsList.get(i).getDebitdocdetailid().longValue();
                TrialDebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge1(finalDebitDocument, custChargeDetails.getChargeid(), custChargeDetails.getDiscount(),docDetailId);
                if (debitDocumentTAXRel != null) {
                    debitDocumentTAXRel.setPlanName("");
                    debitDocumentTAXRels.add(debitDocumentTAXRel);
                }
                i = i + 1;
            }
            logger.info("*********message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
            return debitDocument;
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while generate exception at create invoice for customer charge: "+ex.getMessage());
        }
        return null;
    }
}
