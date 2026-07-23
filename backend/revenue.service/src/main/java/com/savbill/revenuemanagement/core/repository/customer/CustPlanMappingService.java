package com.savbill.revenuemanagement.core.repository.customer;


import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.StatusConstants;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.CustomerServiceMapping;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.prepaid.DbrService;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.CustPlanMapppingPojo;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupRepository;
//import com.savbill.revenuemanagement.productmanagement.qosPolicy.repository.QOSPolicyRepository;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;

import com.savbill.revenuemanagement.rabbitmq.messages.AutoRenewalBoosterPlanMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.CustomerPackageRelMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateCustplanMappingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.PlanUpdateCafApprovalMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.UpdateCustomerCprDateAndStatus;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustPlanMappingService extends AbstractService<CustPlanMappping, CustPlanMapppingPojo, Long> {

    private static final Logger logger = LoggerFactory.getLogger(CustPlanMappingService.class);

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

//    @Autowired
//    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private CustomerMapper customerMapper;

//    @Autowired
//    private CustQuotaService custQuotaService;

    //@Autowired
   // private MessageSender messageSender;

    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerServiceMapRepository customerServiceMappingRepository;
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private DebitDocRepository debitDocumentRepository;

    @Autowired
    private CreditDocService creditDocService;


    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    DbrService dbrService;

    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;

    @Autowired
    KafkaMessageSender kafkaMessageSender;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected JpaRepository<CustPlanMappping, Long> getRepository() {
        return null;
    }
    public CustPlanMappping save(CustPlanMappping entity, String operation) {
        CustPlanMappping custPlanMappping = custPlanMapppingRepository.save(entity);
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        return custPlanMappping;
    }
    public CustPlanMapppingPojo convertDomainToDto(CustPlanMappping custPlanMappping) {
        CustPlanMapppingPojo pojo = new CustPlanMapppingPojo();
        if (custPlanMappping != null) {
            pojo.setId(custPlanMappping.getId());
            pojo.setPlanId(custPlanMappping.getPlanId());
            pojo.setCustid(custPlanMappping.getCustomer().getId());
            pojo.setStartDate(custPlanMappping.getStartDate());
            pojo.setEndDate(custPlanMappping.getEndDate());
            pojo.setExpiryDate(custPlanMappping.getExpiryDate());
            pojo.setStatus(custPlanMappping.getStatus());
   //         pojo.setQospolicyId(null != custPlanMappping.getQospolicy() ? custPlanMappping.getQospolicy().getId() : null);
            pojo.setUploadqos(custPlanMappping.getUploadqos());
            pojo.setUploadts(custPlanMappping.getUploadts());
            pojo.setDownloadqos(custPlanMappping.getDownloadqos());
            pojo.setDownloadts(custPlanMappping.getDownloadts());
            pojo.setService(custPlanMappping.getService());
            pojo.setIsDelete(custPlanMappping.getIsDelete());
     //       pojo.setQuotaList(custQuotaService.convertQuotaDomainListToQuotaPojoList(custPlanMappping.getQuotaList()));
            pojo.setOfferPrice(custPlanMappping.getOfferPrice());
            pojo.setTaxAmount(custPlanMappping.getTaxAmount());
            pojo.setCreditdocid(custPlanMappping.getCreditdocid());
            pojo.setWalletBalUsed(custPlanMappping.getWalletBalUsed());
            pojo.setPurchaseType(custPlanMappping.getPurchaseType());
            pojo.setOnlinePurchaseId(custPlanMappping.getOnlinePurchaseId());
            pojo.setPurchaseFrom(custPlanMappping.getPurchaseFrom());
            pojo.setValidity(custPlanMappping.getValidity());
            pojo.setCustPlanStatus(custPlanMappping.getCustPlanStatus());
            pojo.setDiscount(custPlanMappping.getDiscount());
            pojo.setGraceDays(custPlanMappping.getGraceDays());
            pojo.setRemarks(custPlanMappping.getRemarks());
            if (custPlanMappping.getPlanGroup() != null)
                pojo.setPlangroupid(custPlanMappping.getPlanGroup().getPlanGroupId());
            pojo.setIsInvoiceCreated(custPlanMappping.getIsInvoiceCreated());
            pojo.setNewAmount(custPlanMappping.getNewAmount());
            pojo.setIsHold(custPlanMappping.getIsHold());
            pojo.setCustServiceMappingId(custPlanMappping.getCustServiceMappingId());
            pojo.setInvoiceType(custPlanMappping.getInvoiceType());
            pojo.setIsContainsCustomerInvoice(custPlanMappping.getIsContainsCustomerInvoice());
            pojo.setCustomerCpr(custPlanMappping.getCustomerCpr());
        }
        return pojo;
    }

    /**
     * Change customer service status
     * @param custServIds
     * @param status
     * @param remark
     * @param isChildCustomer
     * @return
     */
    @Transactional
    public List<CustomerServiceMapping> changeStatusOfCustServices(List<Integer> custServIds, String status, String remark, boolean isChildCustomer,Boolean generatecn) {
        List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServIds);
        Customers customers = customersRepository.findById(customerServiceMappings.get(0).getCustId()).get();
        if(CollectionUtils.isEmpty(customerServiceMappings)) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer service not found!", null);
        }
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServIds);
        List<PlanGroup> planGroups = new ArrayList<>();
        if(!status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || !status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)){
            custPlanMapppingList.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP)
                    || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE));
            customerServiceMappings.removeIf(customerServiceMapping -> customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP)
                    || customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE));
            planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
        }

        try {
            switch (status) {
                case StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD: {
                    dbrService.dbrHoldOnServicePause(custPlanMapppingList.stream().map(x->x.getId().longValue()).collect(Collectors.toList()));
                    if(!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, false);
                        if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if(!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if(!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    //Hold service
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(status);
//                        if(getLoggedInBy().length() > 0) {
//                            customerServiceMapping.setServiceHoldBy(getLoggedInBy());
//                        }
//                        else{
//                            customerServiceMapping.setServiceHoldBy("Stop By Schedular");
//                        }
                        customerServiceMapping.setServiceHoldRemarks(remark);
                        customerServiceMapping.setServiceHoldDate(LocalDateTime.now());

                    });
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME: {
                    dbrService.dbrResumeOnServiceResume(custPlanMapppingList.stream().map(x->x.getId().longValue()).collect(Collectors.toList()));
                    if(!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, true);
                        if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if(!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if(!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    //Hold service
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
//                        customerServiceMapping.setServiceResumeBy(getLoggedInBy());
                        customerServiceMapping.setServiceResumeRemarks(remark);
                        customerServiceMapping.setServiceResumeDate(LocalDateTime.now());
                    });
                }
                break;
                default:{
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if (!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(status);
                        customerServiceMapping.setRemarks(remark);
                    });
                }
            }
            if(!CollectionUtils.isEmpty(custPlanMapppingList)){
                changeStatusOfCustPlans(custPlanMapppingList, status, remark);
            }
            if(!isChildCustomer) {
                List<CustomerServiceMapping> childServices = updateChildService(customerServiceMappings.get(0).getCustId(), customerServiceMappings, status, remark,generatecn);
                if(!CollectionUtils.isEmpty(childServices))
                    customerServiceMappings.addAll(childServices);

            }
            if(!isChildCustomer && generatecn && (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE) && customers.getCreatedate().toLocalDate().isBefore(LocalDate.now()))))
                createCNByCustService(customerServiceMappings, remark);
            customerServiceMappingRepository.saveAll(customerServiceMappings);
            if(status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)){
                if(!customerServiceMappingRepository.existsByCustIdAndStatusNotIn(customers.getId(), Collections.singletonList(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE))) {
                    changeCustomerStatus(Collections.singletonList(customers), StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE);
                }
            }

        }
        catch (CustomValidationException ex) {
            throw new CustomValidationException(ex.getErrCode(),ex.getMessage(), null);
        }
        catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + ex.getMessage(), null);
        }
        return customerServiceMappings;
    }

    public List<CustPlanMappping> getCustPlanMappingByPlanGroup(Customers customers, List<PlanGroup> planGroups, boolean isHold) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerIsAndPlanGroupInAndIsHold(customers, planGroups, isHold);
        return custPlanMapppingList;
    }



    /**
     * Change Customer Plan status
     * @param custPlanMapppings
     * @param status
     * @param remark
     * @return
     */
    @Transactional
    public List<CustPlanMappping> changeStatusOfCustPlans(List<CustPlanMappping> custPlanMapppings, String status, String remark) {
        List<PlanGroup> planGroups = new ArrayList<>();
        if(!status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || !status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE))
            planGroups = custPlanMapppings.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
        try {
            switch (status) {
                case StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD: {
                    if(!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    //Hold service
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE)).collect(Collectors.toList());
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setServiceHoldDate(LocalDateTime.now());
                        custPlanMappping.setCustPlanStatus(status);
                        custPlanMappping.setIsHold(Boolean.TRUE);
                    });
//                    try {
//                        ezBillServiceUtility.deactivateService(custPlanMapppings, 13);
//                    } catch (Exception ex) {
//                        logger.error("Error from ezBill "+ex.getMessage());
//                    }
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME: {
                    //Resume service
                    if(!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, true);
                        if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD)).collect(Collectors.toList());
                    boolean isFuturePlanAvailable = custPlanMapppings.stream().anyMatch(CustPlanMappping -> CustPlanMappping.getStartDate().isAfter(LocalDateTime.now()));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        updateCustPlanEndDate(custPlanMappping.getServiceHoldDate(), LocalDateTime.now(), custPlanMappping, isFuturePlanAvailable);
                        if(custPlanMappping.getServiceHoldDate() != null)
                        {
                            Long daysDiff = ChronoUnit.DAYS.between(custPlanMappping.getServiceHoldDate(), LocalDateTime.now());
                            if (custPlanMappping.getTotalHoldDays() != null)
                                daysDiff = daysDiff + custPlanMappping.getTotalHoldDays();
                            custPlanMappping.setTotalHoldDays(daysDiff.intValue());
                        } else {
                            custPlanMappping.setTotalHoldDays(1);
                        }
                        custPlanMappping.setIsHold(Boolean.FALSE);
//                        if (custPlanMappping.getEzyBillServiceId() != null) {
//                            try {
//                                ezBillServiceUtility.manuallyActivate(custPlanMappping);
//                            } catch (Exception ex) {
//                                logger.error("Error from ezBill "+ex.getMessage());
//                            }
//                        }
                    });
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.STOP:
                case StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE: {
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    //terminate service
                    custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE));
                    if(status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP))
                        custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setCustPlanStatus(status);
                        custPlanMappping.setEndDate(LocalDateTime.now());
                        custPlanMappping.setExpiryDate(LocalDateTime.now());
                        custPlanMappping.setIsVoid(Boolean.TRUE);
                        if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                            custPlanMappping.setStartDate(LocalDateTime.now());
                            custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                            custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                        }
                    });
//                    try {
//                        ezBillServiceUtility.deactivateService(custPlanMapppings, 13);
//                    } catch (Exception ex) {
//                        logger.error("Error from ezBill "+ex.getMessage());
//                    }
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE: {
                    //InGrace service
                    if(!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                            custPlanMapppings = custPlanMapppings.stream().filter(CommonUtils.distinctByKey(CustPlanMappping::getId)).collect(Collectors.toList());
                        }
                    }
                    Integer graceDays = Integer.valueOf(clientServiceRepository.findValueByNameAndMvnoId("graceperiod" ,getMvnoIdFromCurrentStaff()));
                    Long systemPromiseToPayCount = Long.valueOf(clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.PROMISETOPAY_COUNT , getMvnoIdFromCurrentStaff()));
                    custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getPurchaseType().equalsIgnoreCase("Volume Booster") || custPlanMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster")
                            || custPlanMappping.getPurchaseType().equalsIgnoreCase("DTV Addon") || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        Long count = null;
                        if (custPlanMappping.getPromisetopay_renew_count() == null) {
                            count = 0L;
                        } else {
                            count = custPlanMappping.getPromisetopay_renew_count();
                        }
                        if (count < systemPromiseToPayCount) {
                            count = count + 1;
                            LocalDateTime endDate = LocalDateTime.now();
                            custPlanMappping.setEndDate(endDate.plusDays(graceDays));
                            custPlanMappping.setExpiryDate(endDate.plusDays(graceDays));
                            custPlanMappping.setGraceDays(graceDays);
                            custPlanMappping.setPromise_to_pay_remarks(remark);
                            custPlanMappping.setGraceDateTime(custPlanMappping.getEndDate().plusDays(graceDays));
                            custPlanMappping.setPromisetopay_renew_count(count);
                            custPlanMappping.setPromise_to_pay_startdate(LocalDateTime.now());
                            LocalDateTime promiseToPayEndDate = LocalDateTime.now().plusDays(graceDays);
                            custPlanMappping.setPromise_to_pay_enddate(promiseToPayEndDate);
                            custPlanMappping.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE);
                            custPlanMappingRepository.save(custPlanMappping);
                        } else {
                            throw new CustomValidationException(HttpStatus.IM_USED.value(), "Promise to pay has already been used.", null);
                        }

                    });

                }
                break;
                default: {
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setCustPlanStatus(status);
                    });
                }
            }
            if(status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE) || status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) ) {
                if(!CollectionUtils.isEmpty(planGroups) && !CollectionUtils.isEmpty(custPlanMapppings)) {
                    List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
//                    plangroupCustPlans.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)
//                            || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    if(!CollectionUtils.isEmpty(plangroupCustPlans)) {
                        plangroupCustPlans = plangroupCustPlans.stream().peek(custPlanMappping -> custPlanMappping.setPlanGroup(null)).collect(Collectors.toList());
                        custPlanMapppings.addAll(plangroupCustPlans);
                    }
                }
            }
            if(!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    updateEndAndStartDate(custPlanMappping, custPlanMappping.getEndDate());
                });
                return custPlanMappingRepository.saveAll(custPlanMapppings);
            }
            else
                return null;
        }
        catch (CustomValidationException ce){
            throw new CustomValidationException(ce.getErrCode(), ce.getMessage(), null);
        }
        catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + ex.getMessage(), null);
        }
    }



    /**
     * Update Customer plan end date with future plan flag
     * @param fromDate
     * @param toDate
     * @param custPlanMappping
     * @param isFuturePlanAvailable
     * @return CustPlanMappping
     */
    @Transactional
    public CustPlanMappping updateCustPlanEndDate(LocalDateTime fromDate, LocalDateTime toDate, CustPlanMappping custPlanMappping,boolean isFuturePlanAvailable) {
        if(fromDate == null)
            fromDate = LocalDateTime.now();
        if(fromDate == null)
            toDate = LocalDateTime.now();
        Long daysDiff = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate());
        final LocalDateTime endDate = custPlanMappping.getEndDate();
        if(daysDiff>1) {
            if (isFuturePlanAvailable && custPlanMappping.getStartDate().isAfter(LocalDateTime.now()))
                custPlanMappping.setEndDate(endDate.plusDays(daysDiff));
            else if (!isFuturePlanAvailable)
                custPlanMappping.setEndDate(endDate.plusDays(daysDiff));
            else
                custPlanMappping.setEndDate(endDate);
        } else {
            Long timeDiff = ChronoUnit.MINUTES.between(fromDate, LocalDateTime.now());
            if (isFuturePlanAvailable && custPlanMappping.getStartDate().isAfter(LocalDateTime.now()))
                custPlanMappping.setEndDate(endDate.plusMinutes(timeDiff));

            else if (!isFuturePlanAvailable)
                custPlanMappping.setEndDate(endDate.plusMinutes(timeDiff));
            else
                custPlanMappping.setEndDate(endDate);
        }
        if(custPlanMappping.getEndDate().toLocalDate().isAfter(LocalDate.now()))
            custPlanMappping.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
        return custPlanMappping;
    }


    public void updateEndAndStartDate(CustPlanMappping custPlanMappping, LocalDateTime updatedDateTime) {
        custPlanMappping.setEndDate(updatedDateTime);
        custPlanMappping.setExpiryDate(updatedDateTime);
        updateCustPlanEndDateInRadius(custPlanMappping, "");
    }

    public void updateCustPlanEndDateInRadius(CustPlanMappping custPlanMappping, String operation) {
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        if (custPlanMapppingPojo.getGraceDays() != null) {
            custPlanMapppingPojo.setExpiryDate(custPlanMapppingPojo.getExpiryDate().plusDays(custPlanMapppingPojo.getGraceDays()));
            custPlanMapppingPojo.setEndDate(custPlanMapppingPojo.getEndDate().plusDays(custPlanMapppingPojo.getGraceDays()));
        }
        CustomerPackageRelMessage message = new CustomerPackageRelMessage(custPlanMapppingPojo, operation);
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_SERVICE_START_STOP);
        kafkaMessageSender.send(new KafkaMessageData(message, CustomerPackageRelMessage.class.getSimpleName()));

    }


    /**
     * Update child customer
     * @param parentCustid
     * @param customerServiceMappings
     * @param status
     * @param remark
     */
    @Transactional
    public List<CustomerServiceMapping> updateChildService(Integer parentCustid, List<CustomerServiceMapping> customerServiceMappings, String status, String remark,Boolean generateCn) {
        List<CustomerServiceMapping> serviceMappings = new ArrayList<>();
        //child cust details extraction from customers table
//        QCustomers qCustomers = QCustomers.customers;
//        BooleanExpression exp = qCustomers.isNotNull().and(qCustomers.parentCustomers.id.eq(parentCustid));
//        exp = exp.and(qCustomers.status.equalsIgnoreCase("Active"));
        Customers parentCust = customersRepository.findById(parentCustid).get();
        List<Customers> childCust = customersRepository.findAllByParentCustomersAndStatus(parentCust,"Active");
        if(!CollectionUtils.isEmpty(childCust)) {
            List<List<CustomerServiceMapping>> customerServiceMappingList = childCust.stream().map(Customers::getCustomerServiceMappingList).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(customerServiceMappingList)) {
                for (List<CustomerServiceMapping> list: customerServiceMappingList) {
                    List<Long> existingIds = customerServiceMappings.stream()
                            .map(CustomerServiceMapping::getServiceId)
                            .collect(Collectors.toList());
                    List<Integer> custServiceIds = list.stream()
                            .filter(custServiceMapping ->
                                    custServiceMapping.getInvoiceType()!= null &&
                                            existingIds.contains(custServiceMapping.getServiceId()) &&
                                            custServiceMapping.getInvoiceType().equalsIgnoreCase(StatusConstants.INVOICE_TYPE.GROUP)).map(CustomerServiceMapping::getId).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(custServiceIds)){
                        List<CustomerServiceMapping>  childServices = changeStatusOfCustServices(custServiceIds, status, remark, true,generateCn);
                        if(!CollectionUtils.isEmpty(childServices))
                            serviceMappings.addAll(childServices);
                    }
                }
            }
        }
        return serviceMappings;
    }

    @Transactional
    public void createCNByCustService(List<CustomerServiceMapping> customerServiceMappings, String remarks) {
        List<Integer> custServiceIds = customerServiceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(custServiceIds)) {
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.getDebitDocIdByCustServiceMappingIdInCprIds(custServiceIds);
            if(!CollectionUtils.isEmpty(custPlanMapppings)) {
                List<Integer> debitDocIds = custPlanMapppings.stream().map(CustPlanMappping::getDebitdocid).filter(Objects::nonNull).map(Long::intValue).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(debitDocIds)) {
                    List<DebitDocument> debitDocuments = debitDocumentRepository.findAllByIdInAndBillrunstatusIsNot(debitDocIds, StatusConstants.INVOICE_STATUS.VOID);
                    for (DebitDocument debitDocument: debitDocuments) {
                        creditDocService.creatCreditNotAsPerService(debitDocument,null, customerServiceMappings, remarks, Boolean.FALSE, null,null,null, null,null);
                    }
                }
            }
        }
    }


    public List<Customers> changeCustomerStatus(List<Customers> customers, String status) {
        try {
            switch (status) {
                default:{
                    customers = customers.stream().peek(customer -> customer.setStatus(status)).collect(Collectors.toList());
                }
            }
            customersRepository.saveAll(customers);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer status: "+ex.getMessage(), null);
        }
        return customers;
    }

    public void updateCprDateAndStatus(UpdateCustomerCprDateAndStatus message) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(message.getId() != null) {
            if(!CollectionUtils.isEmpty(message.getCustPlanMapppingList())) {
                List<Integer> cprIds = message.getCustPlanMapppingList().stream().map(CustPlanMapppingPojo::getId).collect(Collectors.toList());
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByIdIn(cprIds);
                List<CustPlanMappping> custPlanMapppingArrayList = new ArrayList<>();
                boolean isTrailPlan = false;
                if(!CollectionUtils.isEmpty(custPlanMapppings)) {
                    for (CustPlanMapppingPojo planMappping: message.getCustPlanMapppingList()){
                        Optional<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findById(planMappping.getId());
                        LocalDateTime startDate = new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getStartDateString());
                        LocalDateTime endDate = new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getEndDateString());
                        LocalDateTime expiryDate;
                        if(planMappping.getExpiryDateString() != null)
                            expiryDate = new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getExpiryDateString());
                        else {
                            expiryDate = null;
                        }
                        String status = planMappping.getCustPlanStatus();
                        isTrailPlan = planMappping.getIstrialplan();
                        custPlanMappping.get().setStartDate(startDate);
                        custPlanMappping.get().setEndDate(endDate);
                        if(expiryDate != null)
                            custPlanMappping.get().setExpiryDate(expiryDate);
                        custPlanMappping.get().setCustPlanStatus(status);
                        custPlanMappping.get().setIstrialplan(isTrailPlan);
                        custPlanMappping.get().setIsInvoiceCreated(false);
                        custPlanMappping.get().setTrialPlanValidityCount(planMappping.getTrialPlanValidityCount());
                        custPlanMappping.get().setIsTrialValidityDays(planMappping.getIsTrialValidityDays());
                        custPlanMapppingArrayList.add(custPlanMappping.get());
                    }
                    custPlanMapppingArrayList = custPlanMappingRepository.saveAll(custPlanMapppingArrayList);
                    Customers customers = custPlanMapppingArrayList.get(0).getCustomer();
                    if(customers.getIstrialplan()) {
                        Integer count = custPlanMappingRepository.countAllByCustomerAndIstrialplan(customers,true);
                        if (count>0){
                            //no need of saving
                            customers.setIstrialplan(true);
                        }else {
                            if (customers.getEarlyBillDay() != null) {
                                LocalDate currentDate = LocalDate.now();
                                int currentDayOfMonth = currentDate.getDayOfMonth();
                                if (currentDayOfMonth > customers.getEarlyBillDay()) {
                                    customers.setEarlyBilldate(currentDate.plusDays(1));
                                } else {
                                    customers.setEarlyBilldate(currentDate.withDayOfMonth(customers.getEarlyBillDay()));
                                }
                            }
                            customers.setIstrialplan(isTrailPlan);
                            customers.setStatus(Constants.CUSTOMER_STATUS_ACTIVE);
                            customersRepository.save(customers);
                        }
                        CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                        Map<String, Object> data = new HashMap<>();
                        data.put(CustomerBillingMessage.CUST_ID, customers.getId());
                        data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF,customers.getCreatedById());
                        if(customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                            data.put(CustomerBillingMessage.POSTPAIDADVANCE, "Advance");
                        }
                        customerBillingMessage.setData(data);
                        customerBillingMessage.setCustType(customers.getCusttype());
                        customerBillingMessage.setTrailPlanFromToday(message.getCustPlanMapppingList().get(0).isTrailPlanFromToday());
                        customerBillingMessage.setTrailPlanFromTrailDay(message.getCustPlanMapppingList().get(0).isTrailPlanFromTrailDay());
                        messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
                    }
                }
            }
        }
    }


//    public CustPlanMappping convertDTOToDomain(CustPlanMapppingPojo custPlanMapppingPojo) {
//        CustPlanMappping custPlanMappping = new CustPlanMappping();
//        if (custPlanMapppingPojo != null) {
//            custPlanMappping.setBillableCustomerId(custPlanMapppingPojo.getBillableCustomerId());
//            custPlanMappping.setId(custPlanMapppingPojo.getId());
//            custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
//            custPlanMappping.setStartDate(custPlanMapppingPojo.getStartDate());
//            custPlanMappping.setEndDate(custPlanMapppingPojo.getEndDate());
//            custPlanMappping.setExpiryDate(custPlanMapppingPojo.getExpiryDate());
//            custPlanMappping.setStatus(custPlanMapppingPojo.getStatus());
//            if (custPlanMapppingPojo.getQospolicyId() != null)
//                custPlanMappping.setQospolicy(qosPolicyRepository.findById(custPlanMapppingPojo.getQospolicyId()).get());
//
//            custPlanMappping.setUploadqos(custPlanMapppingPojo.getUploadqos());
//            custPlanMappping.setUploadts(custPlanMapppingPojo.getUploadts());
//            custPlanMappping.setDownloadqos(custPlanMapppingPojo.getDownloadqos());
//            custPlanMappping.setDownloadts(custPlanMapppingPojo.getDownloadts());
//            custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());
//            custPlanMappping.setService(custPlanMapppingPojo.getService());
//      //      custPlanMappping.setQuotaList(custQuotaService.convertQuotaPojoListToQuotaDomainList(custPlanMapppingPojo.getQuotaList()));
//            custPlanMappping.setCustomer(customerMapper.dtoToDomain(custPlanMapppingPojo.getCustomer(), new CycleAvoidingMappingContext()));
//            custPlanMappping.setDiscount(custPlanMapppingPojo.getDiscount());
//            custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
//            custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
//            custPlanMappping.setNewAmount(custPlanMapppingPojo.getNewAmount());
//            custPlanMappping.setTaxAmount(custPlanMapppingPojo.getTaxAmount());
//            custPlanMappping.setPurchaseFrom(custPlanMapppingPojo.getPurchaseFrom());
//            custPlanMappping.setPurchaseType(custPlanMapppingPojo.getPurchaseType());
//            custPlanMappping.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
//            custPlanMappping.setOfferPrice(custPlanMapppingPojo.getOfferPrice());
//            custPlanMappping.setIsHold(custPlanMapppingPojo.getIsHold());
//            if (custPlanMapppingPojo.getPlangroupid() != null) {
//                Optional<PlanGroup> plangroup = planGroupRepository.findById(custPlanMapppingPojo.getPlangroupid());
//                if (plangroup.isPresent()) custPlanMappping.setPlanGroup(plangroup.get());
//            }
//            custPlanMappping.setPlanValidityDays(custPlanMapppingPojo.getPlanValidityDays());
//            custPlanMappping.setCustRefName(custPlanMapppingPojo.getCustRefName());
//            custPlanMappping.setIsinvoicestop(custPlanMapppingPojo.getIsinvoicestop());
//            custPlanMappping.setIstrialplan(custPlanMapppingPojo.getIstrialplan());
//            if (custPlanMappping.getIstrialplan()) {
//                custPlanMappping.setIsTrialValidityDays(custPlanMapppingPojo.getIsTrialValidityDays());
//                custPlanMappping.setTrialPlanValidityCount(custPlanMapppingPojo.getTrialPlanValidityCount());
//            } else if (!custPlanMappping.getIstrialplan()) {
//                custPlanMappping.setIsTrialValidityDays(0.0);
//                custPlanMappping.setTrialPlanValidityCount(0);
//
//
//            }
//            custPlanMappping.setIsInvoiceCreated(custPlanMapppingPojo.getIsInvoiceCreated());
//            if (custPlanMapppingPojo.getCustServiceMappingId() != null) {
//                custPlanMappping.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());
//            }
//            if (custPlanMapppingPojo.getInvoiceType() != null) {
//                custPlanMappping.setInvoiceType(custPlanMapppingPojo.getInvoiceType());
//            }
//            if (custPlanMapppingPojo.getTraildebitdocid() != null) {
//                custPlanMappping.setTraildebitdocid(custPlanMapppingPojo.getTraildebitdocid());
//            }
//            if(custPlanMapppingPojo.getRenewalId() != null) {
//                custPlanMappping.setRenewalId(custPlanMapppingPojo.getRenewalId());
//            }
//
//            custPlanMappping.setIsContainsCustomerInvoice(custPlanMapppingPojo.getIsContainsCustomerInvoice());
//            custPlanMappping.setCustomerCpr(custPlanMapppingPojo.getCustomerCpr());
//            custPlanMappping.setSerialNumber(custPlanMapppingPojo.getSerialNumber());
//            custPlanMappping.setServiceId(custPlanMapppingPojo.getServiceId());
//        }
//        return custPlanMappping;
//    }

    public String updatePlanWhileCafApproval(List<PlanUpdateCafApprovalMessage> planUpdateCafApprovalMessages) {
        if (planUpdateCafApprovalMessages == null || planUpdateCafApprovalMessages.isEmpty()) {
            logger.warn("No plan update messages provided.");
            return "No data to process.";
        }
        int successCount = 0;
        for (PlanUpdateCafApprovalMessage dataMessage : planUpdateCafApprovalMessages) {
            try {
                if (dataMessage.getCprId() == null) {
                    logger.error("Missing CPR ID in PlanUpdateCafApprovalMessage: {}", dataMessage);
                    continue;
                }
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findByCprId(dataMessage.getCprId().longValue());
                if (custPlanMappping == null) {
                    logger.warn("No mapping found for CPR ID: {}", dataMessage.getCprId());
                    continue;
                }
                if (dataMessage.getStartDate() != null && dataMessage.getEndDate() != null && dataMessage.getExpiryDate() != null) {
                    custPlanMappping.setStartDate(dataMessage.getStartDate());
                    custPlanMappping.setEndDate(dataMessage.getEndDate());
                    custPlanMappping.setExpiryDate(dataMessage.getExpiryDate());
                    custPlanMappingRepository.save(custPlanMappping);
                    successCount++;
                } else {
                    logger.warn("Incomplete dates for CPR ID: {}", dataMessage.getCprId());
                }
            } catch (Exception e) {
                logger.error("Error updating plan for CPR ID {}: {}", dataMessage.getCprId(), e.getMessage(), e);
            }
        }
        return "Updated " + successCount + " customer plan(s) successfully.";
    }


    public void updateAutoRenewalForBooster(AutoRenewalBoosterPlanMessage message) {
        try {

            int updated = custPlanMapppingRepository.updateRenewalForBoosterById(
                    message.getRenewalForBooster(),
                    message.getCustPlanMappingId()
            );

            if (updated == 0) {
                logger.warn("No record found to update for custPlanMappingId={}", message.getCustPlanMappingId());
                throw new RuntimeException("No plan found with ID: " + message.getCustPlanMappingId());
            }

            logger.info("Successfully updated renewalForBooster for custPlanMappingId={}", message.getCustPlanMappingId());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error occurred while updating renewalForBooster for custPlanMappingId={}: {}",
                    message.getCustPlanMappingId(), e.getMessage(), e);
            throw new RuntimeException("Error updating plan status", e);
        }
    }


    public void updateCustPlanMapping(UpdateCustplanMappingMessage message) {
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (CustPlanMapppingDto dto : message.getCustPlanMapppingDtos()) {
                CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(dto.getId()).get();
                custPlanMappping.setStartDate(LocalDateTime.parse(dto.getStartDateString(), formatter2));
                custPlanMappping.setEndDate(LocalDateTime.parse(dto.getEndDateString(), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(dto.getExpirydateString(), formatter));
                custPlanMappping.setCustPlanStatus(dto.getCustPlanStatus());
                custPlanMappping.setRemarks(dto.getRemarks());
                custPlanMapppingList.add(custPlanMappping);
            }
            custPlanMapppingRepository.saveAll(custPlanMapppingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
