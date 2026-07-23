package com.savbill.radius.services.impl;

import com.savbill.radius.CronJobs.PostPaidPlanExpireryJob;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.repository.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomersServiceImpl {

    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerLocationMappingRepository customerLocationMappingRepository;

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;

    @Autowired
    private CustPlanMappingServiceImpl custPlanMappingService;
    @Autowired
    private PostPaidPlanExpireryJob postPaidPlanExpireryJob;


    @Autowired
    CustIpMappingRepo custIpMappingRepo;
    private static final Logger log = LoggerFactory.getLogger(CustomersServiceImpl.class);

    @Transactional
    public Customers saveSubscriber(CustomMessage message) {
        try {
            String parentQuotaDtls = "individual";
            Map<String, Object> data = message.getCustomerData();

            if (message.getCustomerData() != null) {
                Customers customers = new Customers(message);
                if (data.get("parentcustid") != null) {
                    Customers parentCustomers = customersRepository.getOne(Integer.parseInt(data.get("parentcustid").toString()));
                    if (parentCustomers != null)
                        customers.setParentCustomers(parentCustomers);
                }
                Customers customer = customersRepository.save(customers);
                List<MacAddressMapping> macAddressMappings = new ArrayList<>();
                if (customer.getMacAddressMappingList().size() > 0) {
                    for (MacAddressMapping macAddressMapping : customer.getMacAddressMappingList()) {
                        MacAddressMapping macAddressMappingValue = new MacAddressMapping();
                        macAddressMappingValue.setMacAddressId(macAddressMapping.getMacAddressId());
                        macAddressMappingValue.setMacAddress(macAddressMapping.getMacAddress());
                        macAddressMappingValue.setCustomerId(customer.getId().longValue());
                        macAddressMappingValue.setCreatedBy(customer.getCreatedByName());
                        macAddressMappingValue.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
                        macAddressMappingValue.setLastModificationDate(Timestamp.valueOf(LocalDateTime.now()));
                        macAddressMappingValue.setLastModifiedBy(customer.getCreatedByName());
                        macAddressMappingValue.setNormalizeMac(normalizeMacAddress(macAddressMapping.getMacAddress()));
                        macAddressMappings.add(macAddressMappingValue);
                    }
                }
                if (data.get("custLocationMappingList") != null) {
                    List custLocMap = (List) data.get("custLocationMappingList");
                    if (!CollectionUtils.isEmpty(custLocMap)) {
                        List<CustomerLocationMapping> newcustomerLocationMappings = customerLocationMappingRepository.findByCustId(customer.getId().longValue());
                        if (!CollectionUtils.isEmpty(newcustomerLocationMappings)) {
                            customerLocationMappingRepository.deleteInBatch(newcustomerLocationMappings);
                        }
                        List<CustomerLocationMapping> customerLocationMappings = new ArrayList<>();
                        for (int i = 0; i < custLocMap.size(); i++) {
                            CustomerLocationMapping customerLocationMapping = new CustomerLocationMapping((Map) custLocMap.get(i), customer.getId().longValue());
                            customerLocationMappings.add(customerLocationMapping);
                        }
                        if (!CollectionUtils.isEmpty(customerLocationMappings)) {
                            customerLocationMappingRepository.saveAll(customerLocationMappings);
                        }

                    } else {
                        List<CustomerLocationMapping> newcustomerLocationMappings = customerLocationMappingRepository.findByCustId(customer.getId().longValue());
                        if (!CollectionUtils.isEmpty(newcustomerLocationMappings)) {
                            customerLocationMappingRepository.deleteInBatch(newcustomerLocationMappings);
                        }
                    }
                }
                if (data.get(CommonConstants.CUST_IP_MAPPING_LIST) != null) {
                    List<Map<String, Object>> custIpMappingList = (List<Map<String, Object>>) data.get(CommonConstants.CUST_IP_MAPPING_LIST);
                    List<CustIpMapping> custIpMappings = new ArrayList<>();
                    for (Map<String, Object> custIpMappingData : custIpMappingList) {
                        CustIpMapping custIpMapping = new CustIpMapping();
                        custIpMapping.setId((Integer) custIpMappingData.get("id"));
                        custIpMapping.setIpType((String) custIpMappingData.get("ipType"));
                        custIpMapping.setIpAddress((String) custIpMappingData.get("ipAddress"));
                        custIpMapping.setCustid((Integer) custIpMappingData.get("custid"));

                        // Set other properties similarly
                        custIpMappings.add(custIpMapping);
                    }
                    for (CustIpMapping custIpMapping : custIpMappings) {
                        custIpMappingRepo.save(custIpMapping);
                    }
                }
                macAddressMappingRepository.saveAll(macAddressMappings);
                try {
                    if (data.containsKey("isCustomerCreated") && !data.get("isCustomerCreated").toString().equalsIgnoreCase("true")) {
                        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                        CustomerData custRetrunData = dbAuth.getDBCustomer(null, customer.getMvnoId(), customer.getId().toString(), null, false);
                        changeUserData changeUserData = new changeUserData(customer.getUsername(),
                                Long.valueOf(customer.getMvnoId()));
                        List<changeUserData> userList = new ArrayList<changeUserData>();
                        userList.add(changeUserData);
                        //SUP-1359: As Per ACT if status updated from Update customer then
                        if (customer.getStatus().equalsIgnoreCase("Active"))
                            customerService.CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_ACTIVE);
                        else if (customer.getStatus().equalsIgnoreCase("InActive"))
                            customerService.CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_INACTIVE);
                        else if (customer.getStatus().equalsIgnoreCase("Suspend"))
                            customerService.CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_SUSPEND);
                        else if (customer.getStatus().equalsIgnoreCase("Terminate"))
                            customerService.terminatSessionAfterCustomerStatusChange(customer, customer.getUsername());
                    }
                } catch (Exception e) {
                    log.error("Exception when save customer: " + customer.getUsername() + " status: "
                            + customer.getStatus());
                }
                if (parentQuotaDtls != null)
                    customer.setParentQuotaType(parentQuotaDtls);
                //save Customer plan Data
                if (message.getCustomerData() != null && message.getCustomerData().containsKey("planMappingList") && message.getCustomerData().get("planMappingList") != null) {
                    List<Map<String, Object>> planDataList = (List<Map<String, Object>>) data.get("planMappingList");
                    for (Map<String, Object> planData : planDataList) {
                        planData.put("custid", customer.getId());
                        planData.put("quotaDtls", planData.get("quotaList"));
                        message.setData(planData);
                        if (message.getOperation() == null) {
                            message.setOperation("");
                        }
                        CustPlanMappping mappping = custPlanMappingService.save(message, message.getOperation());
                        postPaidPlanExpireryJob.updateCustomerNextQuotaDate(customers, mappping);
                    }
                }
                return customer;
            } else {
                throw new RuntimeException("INVALID_CUSTOMER_DATA");

            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void CustomerCOADMApigw(CustPlanMappping custPlanMappping, String operation, Long cprId) {
        try {
            log.debug("In CustomerCOADMApigw :" + custPlanMappping);
            if (custPlanMappping != null) {
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                Customers customers = customersRepository.findByCustomerId(custPlanMappping.getCustid());
                CustomerData custRetrunData = dbAuth.getDBCustomer(null, customers.getMvnoId(), custPlanMappping.getCustid().toString(), null, false);
                try {
                    if (custRetrunData != null) {
                        // Issue resolved for JIRA ANG-10451
                        if (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.NEW_VOLUME_BOOSTER)) {
                            List<CustomerPlanData> customerVolumeBooster = custRetrunData.getCustomerVolueBooster();
                            if (!CollectionUtils.isEmpty(customerVolumeBooster)) {
                                if (customerVolumeBooster.size() > 1) {
                                    return;
                                }
                            }
                        }
                        changeUserData changeuserData = new changeUserData(custRetrunData.getUsername(),
                                Long.valueOf(custRetrunData.getMvnoId()), cprId);
                        List<changeUserData> userList = new ArrayList<changeUserData>();
                        userList.add(changeuserData);
                        //Operation check here
                        log.debug("Performing CoA/DM :Operation:" + operation + ":Status:" + custPlanMappping.getCustPlanStatus());
                        if ((custRetrunData.getCustomerBasePlan() == null || CollectionUtils.isEmpty(custRetrunData.getCustomerBasePlan())) && operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CHANGE_PLAN))
                            custRetrunData.setCustomerBasePlan(custRetrunData.getCustomerAllPlan());

                        if (custPlanMappping.getCustPlanStatus().equalsIgnoreCase("stop") && !(operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CHANGE_PLAN)
                                || operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE) || operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE))) {
                            customerService.CoADMSupport(userList, "Remove", custRetrunData, operation);
//                            radaysn.coaDMProcess(userList, "Remove", custRetrunData, operation);
                        } else {
                            customerService.CoADMSupport(userList, "CoA", custRetrunData, operation);
//                            radaysn.coaDMProcess(userList, "COA", custRetrunData, operation);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Exception when coa/dm customer: " + e.getMessage());
                }
            } else {
                log.error("CoA DM Failed Returning Null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("CoA DM Failed :" + e.getMessage());
        }
    }


    public void customMessage(CustomMessage customMessage) {
        setUserProperties(customMessage);
        Customers customers = saveSubscriber(customMessage);
        String parentQuotaDtls = customers.getParentQuotaType();
        if (parentQuotaDtls != null) {
            List<CustQuotaDetails> custQuotaDetails = custQuotaDetailsRepository.findAllByCustid(customers.getId());
            if (!CollectionUtils.isEmpty(custQuotaDetails)) {
                String finalParentQuotaDtls = parentQuotaDtls;
                custQuotaDetails = custQuotaDetails.stream().peek(custQuotaDets -> custQuotaDets.setParentQuotaType(finalParentQuotaDtls)).collect(Collectors.toList());
                custQuotaDetailsRepository.saveAll(custQuotaDetails);
            }
        }
    }


    public void setUserProperties(CustomMessage message) {
        if (message.getCurrentUser() != null) MDC.put("userName", message.getCurrentUser());
        if (message.getTraceId() != null) MDC.put("traceId", message.getTraceId());
        if (message.getSpanId() != null) MDC.put("spanId", message.getSpanId());
    }

    public LocalDate findNearestQuotaResetDateUsingCprId(Long cprId) {
        CustQuotaDetails custQuotaDetailsList = custQuotaDetailsRepository.findByCustPlanMapppingId(cprId);
        return calculateNextQuotaReset(custQuotaDetailsList, LocalDateTime.now());

    }

    public LocalDate calculateNextQuotaReset(CustQuotaDetails custQuotaDetails, LocalDateTime todayDate) {
        Optional<PostpaidPlan> plan = postpaidPlanRepository.findById(custQuotaDetails.getPlanId().intValue());
        if (plan.isPresent()) {
            String quotaResetInterval = plan.get().getQuotaResetInterval();
            LocalDateTime lastQuotaReset = custQuotaDetails.getLastQuotaReset();

            // Default to today if no last reset date is available
            if (lastQuotaReset == null) {
                lastQuotaReset = todayDate;
            }

            switch (quotaResetInterval) {
                case "Daily":
                    return LocalDate.from(lastQuotaReset.plus(1, ChronoUnit.DAYS));
                case "Weekly":
                    return LocalDate.from(lastQuotaReset.plus(7, ChronoUnit.DAYS));
                case "Monthly":
                    return LocalDate.from(lastQuotaReset.plus(1, ChronoUnit.MONTHS));
                default:
                    log.error("Invalid quota reset interval:" + quotaResetInterval);
//                    throw new IllegalArgumentException("Invalid quota reset interval: " + quotaResetInterval);
            }
        }
        return null;
    }

    public String normalizeMacAddress(String macAddress) {
        if (macAddress != null)
            return macAddress.replace(":", "").replace("-", "").replace(".", "");
        return macAddress;
    }
}
