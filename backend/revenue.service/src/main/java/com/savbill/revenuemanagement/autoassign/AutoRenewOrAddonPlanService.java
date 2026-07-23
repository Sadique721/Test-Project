package com.savbill.revenuemanagement.autoassign;


import com.savbill.revenuemanagement.FeignClient.CMSClient;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.SubscriberConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustPayDTOMessage;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.dto.invoice.OnlineInvoicePaymentDTO;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerDtlsPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerInfoPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PlanGroupMappingChargeRelRepo;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerAllInfoPojo;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupMappingRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessageList;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AutoRenewOrAddonPlanService {
    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private DebitDocRepository debitDocumentRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private InvoiceUtil invoiceUtil;

    private static final Logger logger = LoggerFactory.getLogger(AutoRenewOrAddonPlanService.class);

    @Autowired
    TaxService taxService;

    public CustomerWalletPojo getCurrentWalletAmountByCustomerId(Long customerId) {
        CustomerWalletPojo walletPojo = new CustomerWalletPojo();
        List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList = new ArrayList<>();
        Double currentWalletBalance = 0.0;

        List<CreditDocument> creditDocumentList = creditDocRepository.findByCustomerId(customerId.intValue());
        if (!creditDocumentList.isEmpty()) {
            for (CreditDocument creditDocument : creditDocumentList) {
                Double currentCreditDocAmount = 0.0;
                if (!creditDocument.getStatus().equalsIgnoreCase("pending") && !creditDocument.getStatus().equalsIgnoreCase("rejected")) {
                    double docAmount = creditDocument.getAmount() != null ? creditDocument.getAmount() : 0.0;
                    currentCreditDocAmount += docAmount;

                    List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
                    double totalAdjustedAmount = 0.0;

                    if (!creditDebitDocMappingList.isEmpty()) {
                        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappingList) {
                            double adjustedAmount = creditDebitDocMapping.getAdjustedAmount() != null ? creditDebitDocMapping.getAdjustedAmount() : 0.0;
                            currentCreditDocAmount -= adjustedAmount;
                            totalAdjustedAmount += adjustedAmount;
                        }
                    }

                    if (creditDocument.getType().equalsIgnoreCase("Payment")) {
                        if (Double.compare(totalAdjustedAmount, docAmount) != 0) {
                            CreditDocumentPaymentPojo cdWalletPojo = new CreditDocumentPaymentPojo();
                            cdWalletPojo.setId(creditDocument.getId());
                            cdWalletPojo.setRemainingAmount(currentCreditDocAmount);
                            currentWalletBalance += currentCreditDocAmount;
                            creditDocumentPaymentPojoList.add(cdWalletPojo);
                        }
                    }
                }
            }
        }

        walletPojo.setWalletAmount(currentWalletBalance);
        walletPojo.setCreditDocumentPaymentPojos(creditDocumentPaymentPojoList);
        return walletPojo;
    }

    public Map<Long, CustomerWalletPojo> getWalletAmountsByAllCustomerId(List<Long> customerIds) {
        Map<Long, CustomerWalletPojo> walletMap = new HashMap<>();

        if (customerIds == null || customerIds.isEmpty()) {
            return walletMap;
        }

        for (Long customerId : customerIds) {
            CustomerWalletPojo walletPojo = new CustomerWalletPojo();
            List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList = new ArrayList<>();
            double currentWalletBalance = 0.0;

            List<CreditDocument> creditDocumentList = creditDocRepository.findByCustomerId(customerId.intValue());

            if (creditDocumentList != null && !creditDocumentList.isEmpty()) {
                for (CreditDocument creditDocument : creditDocumentList) {
                    if (!"pending".equalsIgnoreCase(creditDocument.getStatus()) &&
                            !"rejected".equalsIgnoreCase(creditDocument.getStatus())) {

                        double docAmount = creditDocument.getAmount() != null ? creditDocument.getAmount() : 0.0;
                        double currentCreditDocAmount = docAmount;

                        List<CreditDebitDocMapping> creditDebitDocMappingList =
                                creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());

                        double totalAdjustedAmount = 0.0;

                        if (creditDebitDocMappingList != null && !creditDebitDocMappingList.isEmpty()) {
                            for (CreditDebitDocMapping mapping : creditDebitDocMappingList) {
                                double adjustedAmount = mapping.getAdjustedAmount() != null ? mapping.getAdjustedAmount() : 0.0;
                                currentCreditDocAmount -= adjustedAmount;

                            }
                        }

                        if ("Payment".equalsIgnoreCase(creditDocument.getType()) && totalAdjustedAmount < docAmount) {

                            CreditDocumentPaymentPojo cdWalletPojo = new CreditDocumentPaymentPojo();
                            cdWalletPojo.setId(creditDocument.getId());
                            cdWalletPojo.setRemainingAmount(currentCreditDocAmount);
                            creditDocumentPaymentPojoList.add(cdWalletPojo);

                            currentWalletBalance += currentCreditDocAmount;
                        }
                    }
                }
            }

            walletPojo.setWalletAmount(currentWalletBalance);
            walletPojo.setCreditDocumentPaymentPojos(creditDocumentPaymentPojoList);

            walletMap.put(customerId, walletPojo);
        }

        return walletMap;
    }

    @Transactional
    public void autoAdjustPaymentAgainstInvoice(CustomerBillingMessage customerBillingMessage, DebitDocument debitDocument, TrialDebitDocument trialDebitDocument) {
        if(customerBillingMessage != null && customerBillingMessage.isAutoPaymentAdjustment()){
            List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList = new ArrayList<>();
            if(customerBillingMessage.getCreditDocumentPaymentPojos() != null && !customerBillingMessage.getCreditDocumentPaymentPojos().isEmpty()){
                creditDocumentPaymentPojoList = customerBillingMessage.getCreditDocumentPaymentPojos();
                creditDocumentPaymentPojoList.sort(Comparator.comparing(CreditDocumentPaymentPojo::getRemainingAmount));
            }

            if(debitDocument!=null) {
                int custId = debitDocument.getCustomer().getId();
                List<String> statusList = Arrays.asList(Constants.PAYMENT_STATUS_APPROVED, Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                List<String> paymentTypeList = Arrays.asList(Constants.PAYMENT_TYPE , Constants.TRANS_CREDIT_NOTE);
                List<String> paytypeList = Arrays.asList(Constants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT , Constants.CREDIT_DOC_TYPE_CREDITNOTE);
                CompletableFuture<List<CreditDocumentDTOForAdjustment>> creditFuture = CompletableFuture.supplyAsync(() ->
                        creditDocRepository.findAllByCustomerInAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(
                                Arrays.asList(custId),paytypeList , statusList, paymentTypeList
                        )
                );

                CompletableFuture<List<DebitDocumentDTOForAdjustment>> debitFuture = CompletableFuture.supplyAsync(() ->
                        debitDocumentRepository.getDebitDocForPaymentAdjustment(Arrays.asList(custId))
                );

                creditFuture.thenCombineAsync(debitFuture, (creditDocs, debitDocs) -> {
                    Map<Integer, List<CreditDocumentDTOForAdjustment>> creditMap = creditDocs.stream()
                            .collect(Collectors.groupingBy(CreditDocumentDTOForAdjustment::getCustId));

                    Map<Integer, List<DebitDocumentDTOForAdjustment>> debitMap = debitDocs.stream()
                            .collect(Collectors.groupingBy(DebitDocumentDTOForAdjustment::getCustId));

                    List<CreditDocumentDTOForAdjustment> credits = creditMap.getOrDefault(custId, Collections.emptyList());
                    List<DebitDocumentDTOForAdjustment> debits = debitMap.getOrDefault(custId, Collections.emptyList());

                    if (!credits.isEmpty() && !debits.isEmpty()) {
                        try {
                            creditDocService.adjustCreditDebitDocs(debits, credits);
                        } catch (Exception e) {
                            logger.error("Error processing customer: " + custId, e);
                        }
                    } else {
                        logger.warn("Either credit or debit is empty for customer: " + custId);
                    }

                    return null;
                });

            }

            if(trialDebitDocument!=null) {
                // renew plan can not in caf
            }
        }
    }


    public void sendAutoRenewOrAddonPlanRequestToCms(AutoRenewOrAddonPlanRequestDto autoRenewOrAddonPlanRequestDto){
        kafkaMessageSender.send(new KafkaMessageData(autoRenewOrAddonPlanRequestDto,autoRenewOrAddonPlanRequestDto.getClass().getSimpleName()));
    }

    public List<CustPlanMapppingDto> getFuturePlanListByCustomerId(Integer custId) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findFuturePlansByCustomerId(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));

        return custPlanMapppingList.stream()
                .map(custPlan -> {
                    CustPlanMapppingDto dto = new CustPlanMapppingDto();
                    BeanUtils.copyProperties(custPlan, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CustPlanMapppingDto> getActivePlanListByCustomerId(Integer custId) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findActivePlanListByCustomerId(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));

        return custPlanMapppingList.stream()
                .map(custPlan -> {
                    CustPlanMapppingDto dto = new CustPlanMapppingDto();
                    BeanUtils.copyProperties(custPlan, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CustPlanMapppingDto> getExpirePlanListByCustomerId(Integer custId) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findExpirePlanListByCustomerId(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));

        return custPlanMapppingList.stream()
                .map(custPlan -> {
                    CustPlanMapppingDto dto = new CustPlanMapppingDto();
                    BeanUtils.copyProperties(custPlan, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    public Double getPlanPriceByPlanId(Integer planId, Integer planGroupId){
        Double planPrice = 0.0;
        Double totalOfferAmount = 0.0;
        Double amount = 0.0;
        List<CustomerChargeHistory> list=new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        try{
            if(planId!=null) {

                Integer planGroupMappingId = null;
                if (planGroupId!=null)
                    planGroupMappingId = planGroupMappingRepository.findPlanGroupMappingByPlanGroupIdAndPlanId(planGroupId,planId);

                PostpaidPlan plan = postpaidPlanRepo.findById(planId).get();
                if(plan.getCategory()!=null && plan.getCategory().equalsIgnoreCase("Business Promotion"))
                {
                    if(plan.getNewOfferPrice()!=null)
                    {
                        return plan.getNewOfferPrice();
                    }
                }

                List<Integer> planChargeIds = postpaidPlanChargeRepo.getChargeListByPlanId(planId);
                List<Charge> charges        = chargeRepository.findByChargeIds(planChargeIds);

                for(Charge charge: charges)
                {
                    amount=0.0;
                    CustomerChargeHistory chargeHistory = new CustomerChargeHistory();
                    chargeHistory.setPlanId(plan.getId());
                    chargeHistory.setPlanName(plan.getName());
                    chargeHistory.setChargeAmount(charge.getActualprice());
                    chargeHistory.setTaxId(charge.getTax().getId());
                    chargeHistory.setChargeId(charge.getId());
                    List<Double>  price=new ArrayList<>();

                    if (planGroupMappingId!=null)
                        price=planGroupMappingChargeRelRepo.findByPlanIdAndChargeIdAndPlanGroupMappingId(planId,charge.getId(),planGroupMappingId);

                    if(price.size() <= 0.0)
                        price=postpaidPlanChargeRepo.getChargeListByPlanIdAndChargeId(planId,charge.getId());

                    if(price!=null && !price.isEmpty())
                        chargeHistory.setChargeAmount(price.get(0));
                    chargeHistory.setDiscount(0.0);
                    if(charge.getTax().getTaxtype().equalsIgnoreCase("TIER") || charge.getTax().getTaxtype().equalsIgnoreCase("Compound")) {
//						chargeHistory.setTaxAmount(0.0);	     //Will not work as set 0 will set 0 in all tax
                        taxService.calculateTierTax(chargeHistory, chargeHistory.getTaxId());
                    }
                    list.add(chargeHistory);
                    amount = chargeHistory.getChargeAmount() + chargeHistory.getTaxAmount()-chargeHistory.getDiscount();
                    totalOfferAmount = totalOfferAmount + amount;
                }
                return Double.valueOf(df.format(totalOfferAmount));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return planPrice;
    }

    public boolean saveProcessTransaction(AirtelAppToCRMDTO airtelAppToCRMDTO, CustPayDTOMessage custPayDTOMessage, String token) throws Exception {
        logger.info("saveProcessTransaction method start");
        boolean isRecordSaved = false;
        CustomerWalletPojo customerWalletPojo = getCurrentWalletAmountByCustomerId(airtelAppToCRMDTO.getCustId() != null ? airtelAppToCRMDTO.getCustId().longValue() : 0);
        Double currentWalletAmount = customerWalletPojo.getWalletAmount();
        if (currentWalletAmount == null) {
            return false;
        }
        if(airtelAppToCRMDTO.getStatus() != null && (airtelAppToCRMDTO.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || airtelAppToCRMDTO.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING))){
            List<TrialDebitDocument> trialDebitDocumentList = CurrentUnpaidAndPartiallyPaidCafInvoicesByCustId(airtelAppToCRMDTO.getCustId());
            for(TrialDebitDocument trialDebitDocument : trialDebitDocumentList) {
                isRecordSaved = adjustExisitingInvoices(custPayDTOMessage, null, trialDebitDocument);
            }
            return isRecordSaved;
        }

        List<DebitDocument> debitDocuments =  CurrentUnpaidAndPartiallyPaidInvoicesByCustId(airtelAppToCRMDTO.getCustId());
        CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
        customerBillingMessage.setAutoPaymentAdjustment(true);
        customerBillingMessage.setCreditDocumentPaymentPojos(customerWalletPojo.getCreditDocumentPaymentPojos());
        if (!debitDocuments.isEmpty()) {
            // Adjust plan
            for (DebitDocument debitDocument : debitDocuments) {
                if (custPayDTOMessage.getMerchantName().toUpperCase().startsWith("PAYWAY_PAYMENT_") || custPayDTOMessage.getMerchantName().toUpperCase().startsWith("TRADELANCE_PAYMENT_")) {
                    if(debitDocument.getTotalamount() > (currentWalletAmount + custPayDTOMessage.getPayment())){
                        throw new CustomValidationException(APIConstants.FAIL, "Amount is not sufficient ", null);
                    }
                }
                    isRecordSaved = adjustExisitingInvoices(custPayDTOMessage, debitDocument, null);
            }
        } else {
            // Renew plan
            List<CustPlanMapppingDto> futurePlans = getFuturePlanListByCustomerId(airtelAppToCRMDTO.getCustId());
            List<CustPlanMapppingDto> activePlans = getActivePlanListByCustomerId(airtelAppToCRMDTO.getCustId());
            List<CustPlanMapppingDto> expiredPlans = getExpirePlanListByCustomerId(airtelAppToCRMDTO.getCustId());

            if (futurePlans != null && !futurePlans.isEmpty()) {
                // Add amount directly into wallet
                creditDocService.addWalletAmount(custPayDTOMessage);
                isRecordSaved = true;
                logger.info("Add Amount directly into wallet for CustomerId :" + airtelAppToCRMDTO.getCustId());
            } else if (activePlans != null && !activePlans.isEmpty()) {
                isRecordSaved = RenewOrAddonPlans(activePlans, airtelAppToCRMDTO.getCustId(), currentWalletAmount, customerWalletPojo, custPayDTOMessage, token ,true);
            } else if (expiredPlans != null && !expiredPlans.isEmpty()) {
                isRecordSaved = RenewOrAddonPlans(expiredPlans, airtelAppToCRMDTO.getCustId(), currentWalletAmount, customerWalletPojo, custPayDTOMessage, token, false);
            } else {
                logger.info("Customer has no plan with custID: {}", airtelAppToCRMDTO.getCustId());
            }
        }

        logger.info("saveProcessTransaction method end");
        return isRecordSaved;
    }

    private boolean RenewOrAddonPlans(List<CustPlanMapppingDto> PlanList, Integer custId, Double currentWalletAmount, CustomerWalletPojo customerWalletPojo, CustPayDTOMessage custPayDTOMessage, String token, boolean isActivPlan) {
        final boolean[] isRecordSaved = {false};
        Long autoRenewNumberOfDaysBeforeExpiry = -1L;
        Optional<CustPlanMapppingDto> latestPlan = PlanList.stream()
                .max(Comparator.comparing(CustPlanMapppingDto::getEndDate));

        latestPlan.ifPresent(plan -> {
            Double planAmount = getPlanPriceByPlanId(plan.getPlanId(), null);
            if (custPayDTOMessage.getMerchantName().toUpperCase().startsWith("PAYWAY_PAYMENT_")  || custPayDTOMessage.getMerchantName().toUpperCase().startsWith("TRADELANCE_PAYMENT_")) {
                if(planAmount > (currentWalletAmount + custPayDTOMessage.getPayment())){
                    throw new CustomValidationException(APIConstants.FAIL, "Amount is not sufficient ", null);
                }
            }
            try {
                creditDocService.addWalletAmount(custPayDTOMessage);
                isRecordSaved[0] = true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (planAmount > 0.0 && (currentWalletAmount + custPayDTOMessage.getPayment())  >= planAmount) {
                isRecordSaved[0] = compareAndSendPayloadForAutoRenewalToCms(custId,plan,customerWalletPojo,autoRenewNumberOfDaysBeforeExpiry,isActivPlan,false, token);
            }
        });
        return isRecordSaved[0];
    }

    public boolean compareAndSendPayloadForAutoRenewalToCms(Integer customerId,CustPlanMapppingDto dto,CustomerWalletPojo customerWallet,Long days,Boolean isActivePlan,Boolean isRequestFromScheduler, String token){
        boolean isSend = false;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if(dto.getPlanId()!=null){
            if(!isRequestFromScheduler){
                AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                renewOrAddonPlanRequestDto.setCustomerId(customerId);
                renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                renewOrAddonPlanRequestDto.setAddonEndDate(null);
                renewOrAddonPlanRequestDto.setAddonStartDate(null);
                renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                renewOrAddonPlanRequestDto.setIsParent(true);
                renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                renewOrAddonPlanRequestDto.setDiscount(0.0);
                renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos());
                kafkaMessageSender.send(new KafkaMessageData(renewOrAddonPlanRequestDto, renewOrAddonPlanRequestDto.getClass().getSimpleName()));
                //genericDataDTO = cmsClient.autoApprovalPayment(renewOrAddonPlanRequestDto, token);
                isSend = true;
            }
        }
        return isSend;
    }

    public List<DebitDocument> CurrentUnpaidAndPartiallyPaidInvoicesByCustId(Integer custId){
        List<String> excludedStatuses = Arrays.asList(Constants.DEBIT_DOC_STATUS.VOID, Constants.DEBIT_DOC_STATUS.CANCELLED);
        List<String> paymentStatuses = Arrays.asList(Constants.DEBIT_DOC_STATUS.UNPAID, Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);

        return debitDocumentRepository.findDebitDocumentsByCustId(custId, excludedStatuses, paymentStatuses);
    }

    public List<TrialDebitDocument> CurrentUnpaidAndPartiallyPaidCafInvoicesByCustId(Integer custId) {
        List<String> excludedStatuses = Arrays.asList(Constants.DEBIT_DOC_STATUS.VOID, Constants.DEBIT_DOC_STATUS.CANCELLED);
        List<String> paymentStatuses = Arrays.asList(Constants.DEBIT_DOC_STATUS.UNPAID, Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);

        return trialDebitDocRepository.findDebitDocumentsByCustId(custId, excludedStatuses,paymentStatuses);
    }

        public boolean adjustExisitingInvoices(CustPayDTOMessage custPayDTOMessage, DebitDocument debitDocument, TrialDebitDocument trialDebitDocument) throws Exception {
        boolean isSaved = false;
        if(debitDocument!=null) {
            logger.info("Active customer is found going for adjustment : " + custPayDTOMessage);
            RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForAddWallet(custPayDTOMessage.getCustId() , custPayDTOMessage.getPgTransactionId() , custPayDTOMessage.getPayment() , custPayDTOMessage.getPaymentGatewayName() , custPayDTOMessage.getOrderId().toString(),custPayDTOMessage.getPgTransactionId());
            CreditDocument creditDocument  = creditDocService.save(recordPaymentPojo, false, false, false, custPayDTOMessage.getMvnoid(), custPayDTOMessage.getPartnerId(), null , false , custPayDTOMessage.getCreatedById(), custPayDTOMessage.getCreatedByName());
            creditDocument.setStatus("approved");
            creditDocRepository.save(creditDocument); /**Save status here for approval**/
            creditDocService.addLedgeAfterApproval(creditDocument); /**Auto approve**/
            CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
            List<CreditDocMessage> creditDocMessage = new ArrayList<>();
            CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
            creditDocMessage.add(creditDoc);
            creditDocMessageList.setCreditDocMessageList(creditDocMessage);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            isSaved = creditDocService.isCreditDebitDocAdjusted(debitDocument,creditDocument);
        }

        if(trialDebitDocument!=null) {
            logger.info("CAF customer is found going for caf adjustment");
            OnlineInvoicePaymentDTO onlineInvoicePaymentDTO = new OnlineInvoicePaymentDTO();
            onlineInvoicePaymentDTO.setInvoiceId(trialDebitDocument.getId());
            onlineInvoicePaymentDTO.setCustId(custPayDTOMessage.getCustId());
            onlineInvoicePaymentDTO.setPaymentGatewayName(custPayDTOMessage.getPaymentGatewayName());
            onlineInvoicePaymentDTO.setTransactionNumber(custPayDTOMessage.getOrderId());
            onlineInvoicePaymentDTO.setMvnoId(custPayDTOMessage.getMvnoid());
            onlineInvoicePaymentDTO.setPartnerId(custPayDTOMessage.getPartnerId());
            onlineInvoicePaymentDTO.setCreatedById(custPayDTOMessage.getCreatedById());
            if(custPayDTOMessage.getBuid() != null) {
                onlineInvoicePaymentDTO.setBuId(Arrays.asList(custPayDTOMessage.getBuid().longValue()));
            }
            onlineInvoicePaymentDTO.setIsLco(false);
            onlineInvoicePaymentDTO.setCreatedByName(custPayDTOMessage.getCreatedByName());
            onlineInvoicePaymentDTO.setAmount(custPayDTOMessage.getPayment());
            invoiceUtil.adjustInvoicePaymentFromOnlinePayment(onlineInvoicePaymentDTO);
            isSaved = true;
        }
        return isSaved;
    }

    public List<DebitDocument> getDebitDocumentByCustId(Integer custId) {
        logger.info("getDebitDocumentByCustId method start with custId : {} ", custId);
        List<DebitDocument> debitDocuments = CurrentUnpaidAndPartiallyPaidInvoicesByCustId(custId);
        logger.info("debitDocuments with custId : {} : {} ", custId, debitDocuments);
        return debitDocuments;
    }

    public List<TrialDebitDocument> getTrailDebitDocumentByCustId(Integer custId) {
        logger.info("getDebitDocumentByCustId method start with custId : {} ", custId);
        List<TrialDebitDocument> trialDebitDocuments = CurrentUnpaidAndPartiallyPaidCafInvoicesByCustId(custId);
        logger.info("trialDebitDocuments with custId : {} : {} ", custId, trialDebitDocuments);
        return trialDebitDocuments;
    }

    public Double checkWalletBalanceByCustId(Integer custId) {
        CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
        pojo.setCustId(custId);
        CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext
                .getBean(CustomerLedgerDtlsService.class);
        CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
        if (infoPojo == null) {
            return 0.0;
        }
        CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                infoPojo);
        if (ledgerAllInfoPojo == null || ledgerAllInfoPojo.getCustomerLedgerInfoPojo() == null) {
            return 0.0;
        }
        return -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance();
    }

    public Double checkWalletBalanceByCustIdWithPositiveBalance(Integer custId) {
        CustomerWalletPojo customerWalletPojo = getCurrentWalletAmountByCustomerId(custId.longValue());
        Double currentWalletAmount = customerWalletPojo.getWalletAmount();
        return currentWalletAmount;
    }

    public Double getPlanPriceByDebitDocId(Integer invoiceId){
        return debitDocumentRepository.findOfferPriceByDebitDocumentId(invoiceId).orElse(0.0);
    }

    public String getPlanNameByDebitDocId(Integer invoiceId){
        return debitDocumentRepository.findPlanNameByDebitDocumentId(invoiceId).orElse("");
    }
}
