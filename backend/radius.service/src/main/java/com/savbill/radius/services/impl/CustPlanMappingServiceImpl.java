package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.dto.CustPlanMapppingDto;
import com.savbill.radius.entity.CustPlanMappping;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.entity.PostpaidPlan;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.kafka.message.CustomerPackageRelMessage;
import com.savbill.radius.rabbitmq.message.UpdateCustplanMappingMessage;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustQuotaDetailsRepository;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.repository.QosPolicyRepository;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustPlanMappingServiceImpl {

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CustomersServiceImpl customersService;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomerServiceHelper customerServiceHelper;
    @Autowired
    private QosPolicyRepository qosPolicyRepository;

    private static final Logger log = LoggerFactory.getLogger(CustPlanMappingServiceImpl.class);
    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    public CustPlanMappping save(CustomMessage message, String operation) {
        try {
            if (message.getData() != null) {

                CustPlanMappping custPlanMappping = new CustPlanMappping(message.getData());
                if (custPlanMappping.getCustid() != null) {
//                    custPlanMappping.setTriggerCoaDm(message.isTriggerCoaDm());
                    CustPlanMappping mapping = customerServiceHelper.saveCustPlanMappingForceFully(custPlanMappping);
                    try {
                        //For Bandwidth booster, Volume Booster plans
                        if (message.isTriggerCoaDm() && !message.isCustomerCreated() && !mapping.getCustPlanStatus().equalsIgnoreCase("STOP") && !operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CHANGE_PLAN)) {
                            log.debug("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                            DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                            Customers customers = customersRepository.findByCustomerId(mapping.getCustid());
                            log.info("Get customer details from DB: " + customers.getUsername());
//                        CustomerData custRetrunData=dbAuth.getDBCustomer(null,customers.getMvnoId(),custPlanMappping.getCustid().toString(),"Auth", false);
                            CustomerData custRetrunData = dbAuth.getDBCustomer(customers.getUsername(), customers.getMvnoId(), String.valueOf(customers.getId()), "Auth", false, "Auth", false);
                            log.info("Get customer details from Radius DB: " + customers.getUsername());
                            if (!CollectionUtils.isEmpty(custRetrunData.getCustomerBasePlan()) && custRetrunData.getCustomerBasePlan().size() > 0) {
                                log.info("Customer has base plan for COA/DM: " + custRetrunData.getCustomerBasePlan().get(0).getPlanName() + " remaining quota: " + custRetrunData.getCustomerBasePlan().get(0).getVolumequota());
                                Optional<CustomerPlanData> basePlan = custRetrunData.getCustomerAllPlan().stream().filter(customerPlanData ->
                                        !customerPlanData.getPlanGroup().equalsIgnoreCase("Bandwidthbooster") && !customerPlanData.getPlanGroup().equalsIgnoreCase("Volume Booster")).findFirst();
                                if (basePlan.isPresent())
                                    customersService.CustomerCOADMApigw(mapping, operation, Long.valueOf(basePlan.get().getCustpackageid()));
                                else
                                    customersService.CustomerCOADMApigw(mapping, operation, Long.valueOf(custRetrunData.getCustomerBasePlan().get(0).getCustpackageid()));
                            } else {
                                log.info("Customer Not have base plan for COA/DM: " + mapping.getPlanName());
                                customersService.CustomerCOADMApigw(mapping, operation, mapping.getId());
                            }
                        }
                        ////For change plan
                        else if (mapping.getPurchaseType().equalsIgnoreCase("NEW") && operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CHANGE_PLAN)) {
                            try {
                                //Fetch only base and stop plan as previus base plan status change to STOP
                                List<CustPlanMappping> oldPlans = custPlanMappingRepository.findAllByCustidAndPurchaseTypeAndCustPlanStatus(mapping.getCustid(), "New", "STOP");
                                if (!CollectionUtils.isEmpty(oldPlans)) {
                                    oldPlans = oldPlans.stream().sorted(Comparator.comparingLong(CustPlanMappping::getId).reversed()).collect(Collectors.toList());
                                    log.debug("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + oldPlans.get(0).getId());
                                    customersService.CustomerCOADMApigw(mapping, operation, oldPlans.get(0).getId());
                                } else {
                                    log.debug("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + mapping.getId());
                                    customersService.CustomerCOADMApigw(mapping, operation, mapping.getId());
                                }
                            } catch (Exception e) {
                                log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                                e.printStackTrace();
                            }
                        } else {
                            log.debug("Not Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId() + " trigger COA/DM: " + message.isTriggerCoaDm());
                        }
                    } catch (Exception e) {
                        log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                        e.printStackTrace();
                    }
                    return mapping;
                } else {
                    throw new RuntimeException("Customer can not be null");
                }
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public CustPlanMappping Update(CustomerPackageRelMessage message, String operation) {
        try {
            if (message.getData() != null) {
                Integer id = (Integer) message.getData().get("id");
                Long cprid = id.longValue();
                LocalDateTime endDate = null;
                LocalDateTime expiryDate = null;
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findByCprId(cprid);
                if (message.getData().get("endDate") != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    endDate = LocalDateTime.parse(message.getData().get("endDate").toString(), formatter);
                }
                if (message.getData().get("expiryDate") != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    expiryDate = LocalDateTime.parse(message.getData().get("expiryDate").toString(), formatter);
                }
                if (endDate != null && expiryDate != null) {
                    CustPlanMappping custPlanMappping1 = new CustPlanMappping(cprid, endDate, expiryDate, (Integer) message.getData().get("custid"), (String) message.getData().get("custPlanStatus"));
                    if (!"InGrace".equalsIgnoreCase(custPlanMappping1.getCustPlanStatus())) {
                        custPlanMappping.setCustPlanStatus(custPlanMappping1.getCustPlanStatus());
                    }
                    custPlanMappping.setEndDate(endDate);
                    custPlanMappping.setExpiryDate(expiryDate);

                }
                CustPlanMappping mappping = custPlanMappingRepository.save(custPlanMappping);
                if (message.getData().get("skipQuotaUpdate") != null && (boolean) message.getData().get("skipQuotaUpdate")) {
                    if (!mappping.getCustPlanStatus().equalsIgnoreCase("STOP")) {
                        try {
                            log.error("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                            customersService.CustomerCOADMApigw(mappping, operation, cprid);
                        } catch (Exception e) {
                            log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                            e.printStackTrace();
                        }
                    } else if (mappping.getCustPlanStatus().equalsIgnoreCase("STOP") && (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE) || operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE))) {
                        try {
                            log.error("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                            customersService.CustomerCOADMApigw(mappping, operation, cprid);
                        } catch (Exception e) {
                            log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                            e.printStackTrace();
                        }
                    }

                } else if (mappping.getCustPlanStatus().equalsIgnoreCase("HOLD")) {
                    try {
                        log.error("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                        customersService.CustomerCOADMApigw(mappping, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_HOLD, cprid);
                    } catch (Exception e) {
                        log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                        e.printStackTrace();
                    }
                } else {
                    log.info("skipQuotaUpdate: " + message.getData().get("skipQuotaUpdate"));
                }

                return mappping;
            } else {
                log.error("Exception at to update CPR Operation: " + operation);
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //Update Based on cprId
    public CustPlanMappping Update01(Map message, String operation) {
        try {
            if (message != null) {
                Integer id = Integer.valueOf(message.get("oldCPRId").toString());
                Object endDate1 = message.get("oldEndDate");
                Object expiryDate1 = message.get("oldExpiryDate");
                Integer oldCustid = Integer.valueOf(message.get("oldCustid").toString());
                String status = message.get("oldCustPlanStatus").toString();
                Object oldSkipQuotaUpdate1 = message.get("oldSkipQuotaUpdate");
                Boolean oldSkipQuotaUpdate =null;
                if(oldSkipQuotaUpdate1 != null) {
                    oldSkipQuotaUpdate = Boolean.valueOf(oldSkipQuotaUpdate1.toString());
                }
                Long cprid = id.longValue();
                LocalDateTime endDate = null;
                LocalDateTime expiryDate = null;
                CustPlanMappping custPlanMappping = null;
                int retryCount = 0;
                while (true) {
                    if(retryCount<=2) {
                        custPlanMappping = custPlanMappingRepository.findByCprId(cprid);
                        if (custPlanMappping != null) {
                            break;
                        }
                        Thread.sleep(500);
                    }else {
                        break;
                    }
                    retryCount++;
                }
                if(custPlanMappping!=null) {
                    if (endDate1 != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        endDate = LocalDateTime.parse(endDate1.toString(), formatter);
                    }
                    if (expiryDate1 != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        expiryDate = LocalDateTime.parse(expiryDate1.toString(), formatter);
                    }
                    if (endDate != null && expiryDate != null) {
                        CustPlanMappping custPlanMappping1 = new CustPlanMappping(cprid, endDate, expiryDate, oldCustid, status);
                        custPlanMappping.setCustPlanStatus(custPlanMappping1.getCustPlanStatus());
                        custPlanMappping.setEndDate(endDate);
                        custPlanMappping.setExpiryDate(expiryDate);

                    }
                    CustPlanMappping mappping = custPlanMappingRepository.save(custPlanMappping);
                    if (oldSkipQuotaUpdate != null && (boolean) oldSkipQuotaUpdate) {
                        if (!mappping.getCustPlanStatus().equalsIgnoreCase("STOP")) {
                            try {
                                log.error("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                                customersService.CustomerCOADMApigw(mappping, operation, cprid);
                            } catch (Exception e) {
                                log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                                e.printStackTrace();
                            }
                        } else if (mappping.getCustPlanStatus().equalsIgnoreCase("STOP") && (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE) || operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE))) {
                            try {
                                log.error("Process to Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                                customersService.CustomerCOADMApigw(mappping, operation, cprid);
                            } catch (Exception e) {
                                log.error("Exception at Trigger COA/DM for Operation: " + operation + " mapping id: " + custPlanMappping.getId());
                                e.printStackTrace();
                            }
                        }
                    } else {
                        log.info("skipQuotaUpdate: " + message.get("skipQuotaUpdate"));
                    }
                    return mappping;
                }else {
                    log.info("no record found in CPR table with cprId {}",cprid);
                }
            } else {
                log.error("Exception at to update CPR Operation: " + operation);
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void triggerCOADMonPlanQosUpdate(List<Integer> planIds) {
        try {
            log.info("In Trigger COA/DM for update plan or QOS no of plans: " + planIds.size());
            //fetch cpr based on planId and which are in live-user and set isTriggerCoaDm flag
            List<CustPlanMappping> list = custPlanMappingRepository.fetchCusPlanByIdsAndLiveUSer(planIds);
            log.info("In Trigger COA/DM for update plan or QOS no of cpr: " + list.size());
            list = list.stream().peek(custPlanMappping -> {
                custPlanMappping.setTriggerCoaDm(true);
                custPlanMappping.setOnQuotaExhaustEventName(CommonConstants.EVENTCONSTANTS.PLAN_QOS_UPDATE);
            }).collect(Collectors.toList());
            custPlanMappingRepository.saveAll(list);
        } catch (Exception ex) {
            log.error("Error In Trigger COA/DM for update plan or QOS no of plans: " + planIds.size() + " Error: " + ex.getMessage());
        }
    }

    @Transactional
    public void updateCustPlanOnPlanUpdateUsingJPQL(List<PostpaidPlan> postpaidPlans) {
        try {
            log.info("No Of Plans to Update: " + postpaidPlans.size());
            for (PostpaidPlan plan : postpaidPlans) {
                // Directly update CustPlanMapping
                if (plan.getQosPolicyId() != null && qosPolicyRepository.existsById(plan.getQosPolicyId())) {
                    int updatedPlans = custPlanMappingRepository.updateQosPolicyIdByPlanId(String.valueOf(plan.getQosPolicyId()), plan.getId());
                    if (updatedPlans > 0) {
                        log.info("Updated CustPlanMappings for planId: " + plan.getId());
                    } else {
                        log.debug("No Customer Plans found to update for planId: " + plan.getId());
                    }
                } else {
                    log.error("Qos Not Available For Plan: " + plan.getName());
                }
                // Fetch IDs of updated mappings
                List<Long> cprIds = custPlanMappingRepository.fetchUpdatedCprIds(plan.getId());
                // Directly update CustQuotaDetails if needed
                if (!CollectionUtils.isEmpty(cprIds)) {
                    int updatedQuota = custQuotaDetailsRepository.updateQuotaDetailsByCprIds(Double.valueOf(plan.getQuota()), plan.getQuotaUnit(), plan.getQuotatype(), plan.getUsageQuotaType(), cprIds);
                    log.info("Updated CustQuotaDetails for " + updatedQuota + " records.");
                } else {
                    log.debug("No Customer Quota Details found for planId: " + plan.getId());
                }

            }
        } catch (Exception ex) {
            log.error("Error in updating Customer Plan and Quota on plan update", ex);
            throw new RuntimeException(ex);
        }
    }

    public void updateCustPlanMapping(UpdateCustplanMappingMessage message) {
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (CustPlanMapppingDto dto : message.getCustPlanMapppingDtos()) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(dto.getId()).get();
                custPlanMappping.setStartDate(LocalDateTime.parse(dto.getStartDateString(), formatter2));
                custPlanMappping.setEndDate(LocalDateTime.parse(dto.getEndDateString(), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(dto.getExpirydateString(), formatter));
                custPlanMappping.setCustPlanStatus(dto.getCustPlanStatus());
                custPlanMapppingList.add(custPlanMappping);
            }
            custPlanMappingRepository.saveAll(custPlanMapppingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
