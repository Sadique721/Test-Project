package com.diameter.serviceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diameter.commons.DiameterUtils;
import com.diameter.dto.CustPlanMapppingPojo;
import com.diameter.dto.CustQuotaDtlsPojo;
import com.diameter.kafka.ChangePlanMessage;
import com.diameter.kafka.CustPlanMappingRevenue;
import com.diameter.kafka.CustomerPackageRelMessage;
import com.diameter.kafka.CustomerServiceActivationMessage;
import com.diameter.kafka.CustomerServiceMappingRevenue;
import com.diameter.kafka.CustomerUpdateMessage;
import com.diameter.kafka.SaveCustomerDataShareMessage;
import com.diameter.kafka.UpdateCustomerShareDataMessage;
import com.diameter.model.CustPlanMappping;
import com.diameter.model.CustSmsDetails;
import com.diameter.model.CustVoiceDetails;
import com.diameter.model.Customer;
import com.diameter.model.CustomerPackageRel;
import com.diameter.model.CustomerQuota;
import com.diameter.model.CustomerServiceMapping;
import com.diameter.model.PostpaidPlan;
import com.diameter.model.ServiceArea;
import com.diameter.repository.CustPlanMappingRepository;
import com.diameter.repository.CustSmsDetailsRepository;
import com.diameter.repository.CustVoiceDetailsRepository;
import com.diameter.repository.CustomerRepository;
import com.diameter.repository.CustomerServiceMappingRepository;
import com.diameter.repository.ServiceAreaRepository;
import com.diameter.service.CustomerService;
import com.diameter.stack.DiameterStack;
import com.diameter.util.GenericDiameterProcessor;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;


@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private LocalCacheManagerServiceImpl cacheManagerServiceImpl;

    @Autowired
    private QOSPolicyServiceImpl qosPolicyServiceImpl;

    @Autowired
    private PlanServiceImpl planServiceImpl;


    @Autowired
    private MappingHeaderServiceImpl mappingHeaderServiceImpl;

    @Autowired
    private GenericDiameterProcessor genericDiameterProcessor;

    @Autowired
    private DiameterStack diameterStack;


    private final CustomerRepository repository;
    private final CustPlanMappingRepository custPlanMappingRepository;
    private  final CustomerServiceMappingRepository customerServiceMappingRepository;
    private final ServiceAreaRepository serviceAreaRepository;
    private final CustSmsDetailsRepository custSmsDetailsRepository;
    private final CustVoiceDetailsRepository custVoiceDetailsRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(CustomerRepository repository,
                               CustPlanMappingRepository custPlanMappingRepository,
                               CustomerServiceMappingRepository customerServiceMappingRepository,
                               ServiceAreaRepository serviceAreaRepository,
                               CustSmsDetailsRepository custSmsDetailsRepository,
                               CustVoiceDetailsRepository custVoiceDetailsRepository) {
        this.repository = repository;
        this.custPlanMappingRepository = custPlanMappingRepository;
        this.customerServiceMappingRepository = customerServiceMappingRepository;
        this.serviceAreaRepository = serviceAreaRepository;
        this.custSmsDetailsRepository = custSmsDetailsRepository;
        this.custVoiceDetailsRepository = custVoiceDetailsRepository;
    }

    CustPlanMapppingPojo custPlanMapppingdto = new CustPlanMapppingPojo();
    CustPlanMappping custPlanMappping = new CustPlanMappping();
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();

    public Customer createCustomer(Customer customer) throws ValidationException {
        logger.info("Creating customer with ID: {}", customer.getCustId());
        Customer newCustomer = repository.saveCustomer(customer);
        logger.info("Customer created successfully.");
		return newCustomer;
    }

    public List<Customer> getCustomers(String id, String name, String userName) {
    	logger.info("Get customer details");
    	try {
    		if (id != null) {
                Customer customer = repository.getCustomerByIdWithQuotas(id);
                return customer != null ? Collections.singletonList(customer) : Collections.emptyList();
            } else if (name != null) {
                Customer customer = repository.getCustomerByNameWithQuotas(name);
                return customer != null ? Collections.singletonList(customer) : Collections.emptyList();
            }else if (userName != null) {
                Customer customer = repository.getCustomerByUserNameWithQuotas(userName);
                return customer != null ? Collections.singletonList(customer) : Collections.emptyList();
            } else {
                return repository.getAllCustomers();
            }
    	}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    	return Collections.emptyList();
    }

    @Override
    public Map<String,Object> getCustomers(String id, String name, String userName, int page, int size) {

        try {
            if(id != null || name != null || userName != null){
                List<Customer> customers = getCustomers(id, name, userName);

                Map<String, Object> response = new HashMap<>();
                response.put("content", customers);
                response.put("page", 0);
                response.put("size", customers.size());
                response.put("totalElements", customers.size());
                response.put("totalPages", 1);
                return response;
            }
            return repository.getAllCustomersPaginated(page, size);
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }


	@Override
	public void updateQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, long custPackageId) {
		repository.updateQuotasByCustomerId(usedQuota, custId, planId,custPackageId);
	}
	
	@Override
	public void updateTimeQuotaByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId) {
		repository.updateTimeQuotaByCustomerId(usedQuota, custId, planId);
	}


	@Override
	public void updateSmsQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId) {
		repository.updateSmsQuotasByCustomerId(usedQuota, custId, planId,custPackageId);
	}
	
	@Override
	public void updateVoiceQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId) {
		repository.updateVoiceQuotasByCustomerId(usedQuota, custId, planId,custPackageId);
	}

    @Override
    public List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId, BigInteger planId, BigInteger custPackageId) {
        return repository.getCustomerPackageRel(custId, planId, custPackageId);
    }
    
    @Override
    public List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId) {
        return repository.getCustomerPackageRel(custId);
    }

    @Override
    public List<CustSmsDetails> getCustomerSmsPackageRel(BigInteger custId, BigInteger planId, Long custPackageId) {
        return custSmsDetailsRepository.getByCustPackageId(custPackageId);
    }
    
    @Override
    public List<CustVoiceDetails> getCustomerVoicePackageRel(BigInteger custId, BigInteger planId, Long custPackageId) {
        return custVoiceDetailsRepository.getByCustPackageId(custPackageId);
    }
    
    @Override
	public void updateFupStatusByQuotaId(BigInteger quotaId) {
		repository.updateFupStatusByQuotaId(quotaId);
	}



    @Transactional
    public void saveCustomers(SaveCustomerDataShareMessage message) throws ValidationException{
        try {
            Customer customer = new Customer();
            // Set values from message to customer object
            if(message.getId() != null)
            {
                customer.setCustId(BigInteger.valueOf(message.getId()));
            }
            customer.setTitle(message.getTitle());
            customer.setUserName(message.getUsername());
            customer.setPassword(message.getPassword());
            customer.setFirstName(message.getFirstname());
            customer.setLastName(message.getLastname());
            customer.setCustName(message.getCustname());
            if(message.getNetworkdevicesId() != null)
            {
                customer.setNetworkDeviceId(BigInteger.valueOf(message.getNetworkdevicesId()));
            }
            customer.setCStatus(message.getStatus());
            customer.setCustomerType(message.getCusttype());
            if(message.getMvnoId() != null)
            {
                customer.setMvnoId(BigInteger.valueOf(message.getMvnoId()));
            }
            if(message.getBuId() != null)
            {
                customer.setBuid(BigInteger.valueOf(message.getBuId()));
            }
            customer.setIsDeleted(
                    message.getIsDeleted() != null
                            ? (short) (message.getIsDeleted() ? 1 : 0)
                            : null
            );
            customer.setFramedIpBind(message.getFramedIpBind());

            if(message.getOltportid() != null)
            {
                customer.setOltPortId(BigInteger.valueOf(message.getOltportid()));
            }
            if(message.getOltslotid() != null)
            {
                customer.setOltSlotId(BigInteger.valueOf(message.getOltslotid()));
            }
            customer.setNasPort(message.getNasPort());
            customer.setIpPoolNameBind(message.getIpPoolNameBind());
            customer.setFramedIp(message.getFramedIp());
            if(message.getParnterId() != null)
            {
                customer.setPartnerId(BigInteger.valueOf(message.getParnterId()));
            }
            customer.setParentCustId(message.getParentCustId());
            customer.setCreatedByStaffId(message.getCreatedById());
            if(message.getServiceAreaId() != null)
            {
                ServiceArea serviceArea = serviceAreaRepository.findById(Long.valueOf(message.getServiceAreaId())).orElse(null);
                if(serviceArea != null && serviceArea.getId() != null)
                {
                    customer.setServiceAreaId(BigInteger.valueOf(serviceArea.getId()));
                }
            }
            customer.setBlockNo(message.getBlockNo());
            customer.setLastModifiedByStaffId(message.getLastModifiedById());

            // Last password changes is mandatory so if not found then set current date time.
            if(customer.getLastPasswordChange() == null)
            {
                customer.setLastPasswordChange(LocalDateTime.now());
            }
            // Note : Caf No is required so as discussed with developer set custId in this field.
            customer.setCafNo(String.valueOf(message.getId()));

            //TODO Arpit : Below fields are not coming from CPM service.
            customer.setEmail(message.getEmail());
            customer.setAccountNumber(message.getAccountNumber());
            if(message.getNextbilldate() != null)
            {
                customer.setNextBillDate(LocalDate.parse(message.getNextbilldate()));
            }
            customer.setContactPerson(message.getContactperson());
            customer.setPan(message.getPan());
            customer.setMobile(message.getMobile());
            customer.setCalendarType(message.getCalendarType());
            customer.setUpdateByName(message.getLastModifiedByName());
            customer.setCreateByName(message.getCreatedByName());
            customer.setLastModifiedByStaffId(message.getLastModifiedById());
            customer.setCustCategory(message.getCustcategory());
            if(message.getServiceAreaId() != null)
            {
                customer.setServiceAreaId(BigInteger.valueOf(message.getServiceAreaId()));
            }
            //Added missing fields
            customer.setFailCount(message.getFailCount());
            customer.setCreateDate(new Timestamp(System.currentTimeMillis()));
            if(message.getExpiryDate() != null)
            {
                customer.setExpiryDate(String.valueOf(message.getExpiryDate()));
            }
            customer.setLastStatusChangeDate(message.getLastStatusChangeDate());
            customer.setAddParam1(message.getAddParam1());
            customer.setAddParam2(message.getAddParam2());
            customer.setAddParam3(message.getAddParam3());
            customer.setAddParam4(message.getAddParam4());
            customer.setSelfCarePwd(message.getSelfCarePwd());
            customer.setGst(message.getGst());
            customer.setAadhar(message.getAadhar());
            customer.setMacTelFlag(
                    message.getMactelFlag() != null
                            ? (short) (message.getMactelFlag() ? 1 : 0)
                            : null
            );
            customer.setServiceType(message.getServiceType());
            customer.setVoiceProvision(
                    message.getVoiceProvision() != null
                            ? (short) (message.getVoiceProvision() ? 1 : 0)
                            : null
            );
            customer.setVoipEnableFlag(
                    message.getVoipEnableFlag() != null
                            ? (short) (message.getVoipEnableFlag() ? 1 : 0)
                            : null
            );
            customer.setOnlineRenewalFlag(
                    message.getOnlineRenewalFlag() != null
                            ? (short) (message.getOnlineRenewalFlag() ? 1 : 0)
                            : null
            );
            customer.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            if(message.getWalletBalance() != null)
            {
                customer.setWalletBalance(BigDecimal.valueOf(message.getWalletBalance()));
            }
            customer.setDidNo(message.getDidNo());
            customer.setVoiceSrvType(message.getVoiceSrvType());
            customer.setAltMobile(message.getAltMobile());
            if(message.getMaxConcurrentSession() != null)
            {
                customer.setMaxConcurrentSession(BigInteger.valueOf(message.getMaxConcurrentSession()));
            }
            customer.setMacProvision(
                    message.getMacProvision() != null
                            ? (short) (message.getMacProvision() ? 1 : 0)
                            : null
            );
            customer.setMacAuthEnable(
                    message.getMacAuthEnable() != null
                            ? (short) (message.getMacAuthEnable() ? 1 : 0)
                            : null
            );
            customer.setNextQuotaResetDate(message.getNextQuotaResetDate());
            if(message.getMacRetentionPeriod() != null)
            {
                customer.setMacRetentionPeriod(BigInteger.valueOf(message.getMacRetentionPeriod()));
            }
            customer.setMacRetentionUnit(message.getMacRetentionUnit());

            List<CustPlanMapppingPojo> planMappings = getCustomerCreatePlanMappings(message);

            //Set Customer quota.
            List<CustomerQuota> customerQuotaList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(planMappings)) {

                for (CustPlanMapppingPojo custPlanMapppingPojo : planMappings) {

                    if (custPlanMapppingPojo == null ||
                            custPlanMapppingPojo.getQuotaList() == null ||
                            custPlanMapppingPojo.getQuotaList().isEmpty()) {
                        continue;
                    }

                    for (CustQuotaDtlsPojo custQuotaDtlsPojo : custPlanMapppingPojo.getQuotaList()) {

                        if (custQuotaDtlsPojo == null) {
                            continue;
                        }

                        Integer custPackageId = resolveCustPackageId(custPlanMapppingPojo, custQuotaDtlsPojo);
                        if (custPackageId == null) {
                            logger.warn("Skipping quota id={} for custId={} planId={} because custpackageid is missing",
                                    custQuotaDtlsPojo.getId(),
                                    custPlanMapppingPojo.getCustid(),
                                    custPlanMapppingPojo.getPlanId());
                            continue;
                        }

                        CustomerQuota customerQuota = new CustomerQuota();

                        if (custQuotaDtlsPojo.getId() != null) {
                            customerQuota.setQuotaDtlsId(
                                    BigInteger.valueOf(custQuotaDtlsPojo.getId())
                            );
                        }

                        if (custPlanMapppingPojo.getCustid() != null) {
                            customerQuota.setCustId(
                                    BigInteger.valueOf(custPlanMapppingPojo.getCustid())
                            );
                        }

                        if (custPlanMapppingPojo.getPlanId() != null) {
                            customerQuota.setPlanId(
                                    BigInteger.valueOf(custPlanMapppingPojo.getPlanId())
                            );
                        }

                        customerQuota.setQuotaType(
                                custQuotaDtlsPojo.getQuotaType() != null
                                        ? custQuotaDtlsPojo.getQuotaType()
                                        : ""
                        );

                        customerQuota.setTotalQuota(
                                toBigDecimal(custQuotaDtlsPojo.getTotalQuota())
                        );

                        customerQuota.setUsedQuota(
                                toBigDecimal(custQuotaDtlsPojo.getUsedQuota())
                        );

                        customerQuota.setQuotaUnit(
                                custQuotaDtlsPojo.getQuotaUnit() != null
                                        ? custQuotaDtlsPojo.getQuotaUnit()
                                        : ""
                        );

                        customerQuota.setTimeTotalQuota(
                                toBigDecimal(custQuotaDtlsPojo.getTimeTotalQuota())
                        );

                        customerQuota.setTimeQuotaUsed(
                                toBigDecimal(custQuotaDtlsPojo.getTimeQuotaUsed())
                        );

                        customerQuota.setCustPackageId(custPackageId);

                        customerQuota.setTimeQuotaUnit(custQuotaDtlsPojo.getTimeQuotaUnit());

                        customerQuota.setTotalQuotaKb(
                                toBigDecimal(custQuotaDtlsPojo.getTotalQuotaKB())
                        );

                        customerQuota.setUsedQuotaKb(
                                toBigDecimal(custQuotaDtlsPojo.getUsedQuotaKB())
                        );

                        customerQuota.setTimeUsedQuotaSec(
                                toBigDecimal(custQuotaDtlsPojo.getTimeUsedQuotaSec())
                        );

                        customerQuota.setTimeTotalQuotaSec(
                                toBigDecimal(custQuotaDtlsPojo.getTimeTotalQuotaSec())
                        );

                        customerQuota.setDidTotalQuota(
                                toBigDecimal(custQuotaDtlsPojo.getDidtotalquota())
                        );

                        customerQuota.setDidUsedQuota(
                                toBigDecimal(custQuotaDtlsPojo.getDidusedquota())
                        );

                        customerQuota.setIntercomTotalQuota(
                                toBigDecimal(custQuotaDtlsPojo.getIntercomtotalquota())
                        );

                        customerQuota.setIntercomUsedQuota(
                                toBigDecimal(custQuotaDtlsPojo.getIntercomusedquota())
                        );

                        customerQuota.setDidQuotaUnit(custQuotaDtlsPojo.getDidQuotaUnit());
                        customerQuota.setIntercomQuotaUnit(custQuotaDtlsPojo.getIntercomQuotaUnit());

                        customerQuota.setParentQuotaType(custQuotaDtlsPojo.getParentQuotaType());

                        customerQuota.setIsChunkAvailable(custQuotaDtlsPojo.isChunkAvailable());

                        customerQuota.setReservedQuotaInPer(custQuotaDtlsPojo.getReservedQuotaInPer());
                        customerQuota.setTotalReservedQuota(custQuotaDtlsPojo.getTotalReservedQuota());

                        customerQuota.setUsageQuotaType(custQuotaDtlsPojo.getUsageQuotaType());

                        customerQuota.setSkipQuotaUpdate(
                                custQuotaDtlsPojo.getSkipQuotaUpdate() != null
                                        ? custQuotaDtlsPojo.getSkipQuotaUpdate()
                                        : Boolean.FALSE
                        );

                        customerQuota.setIsDeleted(
                                custQuotaDtlsPojo.getIsDelete() != null
                                        ? custQuotaDtlsPojo.getIsDelete()
                                        : Boolean.FALSE
                        );

                        customerQuota.setCurrentSessionUsageTime(
                                toBigDecimal(custQuotaDtlsPojo.getCurrentSessionUsageTime())
                        );

                        customerQuota.setCurrentSessionUsageVolume(
                                toBigDecimal(custQuotaDtlsPojo.getCurrentSessionUsageVolume())
                        );

                        // ===============================
                        // Date Conversion
                        // ===============================

                        if (custQuotaDtlsPojo.getLastQuotaReset() != null) {
                            customerQuota.setLastQuotaReset(
                                    Timestamp.valueOf(custQuotaDtlsPojo.getLastQuotaReset())
                            );
                        }

                        if(message.getCreatedById() != null)
                        {
                            customerQuota.setCreatedByStaffId(BigDecimal.valueOf(message.getCreatedById()));
                        }
                        if(message.getCreatedByName() != null)
                        {
                            customerQuota.setCreateByName(message.getCreatedByName());
                        }
                        if(message.getLastModifiedByName() != null)
                        {
                            customerQuota.setUpdateByName(message.getLastModifiedByName());
                        }
                        if(message.getLastModifiedById() != null)
                        {
                            customerQuota.setLastModifiedByStaffId(BigDecimal.valueOf(message.getLastModifiedById()));
                        }
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        if(custQuotaDtlsPojo.getCreatedate() != null)
                        {
                            customerQuota.setCreateDate(Timestamp.valueOf(custQuotaDtlsPojo.getCreatedate()));
                        }
                        else
                        {
                            customerQuota.setCreateDate(now);
                        }
                        if(custQuotaDtlsPojo.getUpdatedate() != null)
                        {
                            customerQuota.setLastModifiedDate(Timestamp.valueOf(custQuotaDtlsPojo.getUpdatedate()));
                        }
                        else
                        {
                            customerQuota.setLastModifiedDate(now);
                        }

                        if(custQuotaDtlsPojo.getDidtotalquota() != null)
                        {
                            customerQuota.setDidTotalQuota(BigDecimal.valueOf(custQuotaDtlsPojo.getDidtotalquota()));
                        }
                        if(custQuotaDtlsPojo.getDidusedquota() != null)
                        {
                            customerQuota.setDidUsedQuota(BigDecimal.valueOf(custQuotaDtlsPojo.getDidusedquota()));
                        }
                        if(custQuotaDtlsPojo.getIntercomtotalquota() != null)
                        {
                            customerQuota.setIntercomTotalQuota(BigDecimal.valueOf(custQuotaDtlsPojo.getIntercomtotalquota()));
                        }
                        if(custQuotaDtlsPojo.getIntercomusedquota() != null)
                        {
                            customerQuota.setIntercomUsedQuota(BigDecimal.valueOf(custQuotaDtlsPojo.getIntercomusedquota()));
                        }
                        customerQuota.setSpeedDowngradeFlag(Boolean.FALSE);
                        customerQuotaList.add(customerQuota);
                    }
                }
            }
            customer.setIsinvoicestop(message.getIsinvoicestop());

            // Save the customer using the repository
            Customer savedCustomer = repository.saveCustomer(customer);

            List<CustPlanMappping> custPlanMapppingList = addCustPlanMapping(planMappings, savedCustomer);
            List<CustomerServiceMapping> customerServiceMappingList = addCustServiceMapping(message.getCustomerServiceMappingList(),savedCustomer);

            custPlanMappingRepository.saveAll(custPlanMapppingList);
            saveCustomerQuotas(customerQuotaList);
            customerServiceMappingRepository.saveAll(customerServiceMappingList);
            Map<Long, Long> custPackageIdByPlanId = buildCustPackageIdByPlanId(custPlanMapppingList);

            List<CustSmsDetails> smsDetailsList = resolveSmsDetails(message);
            if (!smsDetailsList.isEmpty()) {
                for (CustSmsDetails smsDetails : smsDetailsList) {
                    smsDetails.setCustomer(savedCustomer);
                    attachCustPlanMapping(smsDetails, custPackageIdByPlanId);
                }
                custSmsDetailsRepository.saveAll(smsDetailsList);
            }
            List<CustVoiceDetails> voiceDetailsList = resolveVoiceDetails(message);
            if (!voiceDetailsList.isEmpty()) {
                for (CustVoiceDetails voiceDetails : voiceDetailsList) {
                    voiceDetails.setCustomer(savedCustomer);
                    attachCustPlanMapping(voiceDetails, custPackageIdByPlanId);
                }
                custVoiceDetailsRepository.saveAll(voiceDetailsList);
            }
        } finally {
            logger.info("Customer processing completed for username={}", message.getUsername());
        }
    }

    public List<CustPlanMappping> addCustPlanMapping(List<CustPlanMapppingPojo> custPlanMapppings,Customer savedCustomer) {
        List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
        if (CollectionUtils.isEmpty(custPlanMapppings)) {
            logger.warn("No customer package rel data found in customer create message for custId={}", savedCustomer != null ? savedCustomer.getCustId() : null);
            return custPlanMapppingList;
        }
        for (CustPlanMapppingPojo custPlanMapppingPojo : custPlanMapppings) {
            this.custPlanMapppingdto.setId(custPlanMapppingPojo.getId());
            this.custPlanMapppingdto.setCustid(custPlanMapppingPojo.getCustid());

            this.custPlanMapppingdto.setPlanId(custPlanMapppingPojo.getPlanId());
            this.custPlanMapppingdto.setPlangroupid(custPlanMapppingPojo.getPlangroupid());
            this.custPlanMapppingdto.setBillTo(custPlanMapppingPojo.getBillTo());
            this.custPlanMapppingdto.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
            this.custPlanMapppingdto.setService(custPlanMapppingPojo.getService());
            this.custPlanMapppingdto.setIsDelete(custPlanMapppingPojo.getIsDelete());
            this.custPlanMapppingdto.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
            this.custPlanMapppingdto.setStatus(custPlanMapppingPojo.getStatus());
            this.custPlanMapppingdto.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());

            //TODO Arpit : Added missing fields
            this.custPlanMapppingdto.setStartDate(getDateTime(custPlanMapppingPojo.getStartDate(), custPlanMapppingPojo.getStartDateString()));
            this.custPlanMapppingdto.setEndDate(getDateTime(custPlanMapppingPojo.getEndDate(), custPlanMapppingPojo.getEndDateString()));
            this.custPlanMapppingdto.setExpiryDate(getDateTime(custPlanMapppingPojo.getExpiryDate(), custPlanMapppingPojo.getExpiryDateString()));
            this.custPlanMapppingdto.setQospolicyId(custPlanMapppingPojo.getQospolicyId());
            this.custPlanMapppingdto.setCreatedate(custPlanMapppingPojo.getCreatedate());
            this.custPlanMapppingdto.setUpdatedate(custPlanMapppingPojo.getUpdatedate());
            this.custPlanMapppingdto.setOfferPrice(custPlanMapppingPojo.getOfferPrice());
            this.custPlanMapppingdto.setTaxAmount(custPlanMapppingPojo.getTaxAmount());
            this.custPlanMapppingdto.setDebitdocid(custPlanMapppingPojo.getDebitdocid());
            this.custPlanMapppingdto.setWalletBalUsed(custPlanMapppingPojo.getWalletBalUsed());
            this.custPlanMapppingdto.setPurchaseType(custPlanMapppingPojo.getPurchaseType());
            this.custPlanMapppingdto.setPurchaseFrom(custPlanMapppingPojo.getPurchaseFrom());
            this.custPlanMapppingdto.setGraceDays(custPlanMapppingPojo.getGraceDays());
            this.custPlanMapppingdto.setServiceId(custPlanMapppingPojo.getServiceId());
            this.custPlanMapppingdto.setIsHold(custPlanMapppingPojo.getIsHold());
            this.custPlanMapppingdto.setCustomerCpr(custPlanMapppingPojo.getCustomerCpr());
            //CreatedById
            if(custPlanMapppingPojo.getCreatedById() != null)
            {
                this.custPlanMapppingdto.setCreatedById(custPlanMapppingPojo.getCreatedById());
            }
            else
            {
                this.custPlanMapppingdto.setCreatedById(savedCustomer.getCreatedByStaffId());
            }

            //LastModifiedById
            if(custPlanMapppingPojo.getLastModifiedById() != null)
            {
                this.custPlanMapppingdto.setLastModifiedById(custPlanMapppingPojo.getLastModifiedById());
            }
            else
            {
                this.custPlanMapppingdto.setLastModifiedById(savedCustomer.getLastModifiedByStaffId());
            }

            //CreatedByName
            if(custPlanMapppingPojo.getCreatedByName() != null && !custPlanMapppingPojo.getCreatedByName().isEmpty())
            {
                this.custPlanMapppingdto.setCreatedByName(custPlanMapppingPojo.getCreatedByName());
            }
            else if(savedCustomer.getCreateByName() != null)
            {
                this.custPlanMapppingdto.setCreatedByName(savedCustomer.getCreateByName());
            }
            else
            {
                this.custPlanMapppingdto.setCreatedByName("System");
            }

            //LastModifiedByName
            if(custPlanMapppingPojo.getLastModifiedByName() != null && !custPlanMapppingPojo.getLastModifiedByName().isEmpty())
            {
                this.custPlanMapppingdto.setLastModifiedByName(custPlanMapppingPojo.getLastModifiedByName());
            }
            else if(savedCustomer.getUpdateByName() != null)
            {
                this.custPlanMapppingdto.setLastModifiedByName(savedCustomer.getUpdateByName());
            }
            else
            {
                this.custPlanMapppingdto.setLastModifiedByName("System");
            }

            this.custPlanMappping = convertDtoToDomainCustPlanMapping(this.custPlanMapppingdto);
            custPlanMapppingList.add(custPlanMappping);
        }
        return custPlanMapppingList;
    }

    private List<CustPlanMapppingPojo> getCustomerCreatePlanMappings(SaveCustomerDataShareMessage message) {
        List<CustPlanMapppingPojo> packageMappings = message.getCustPlanMapppingList();
        if (CollectionUtils.isEmpty(packageMappings)) {
            packageMappings = message.getPlanMappingList();
        }

        Map<Integer, CustPlanMapppingPojo> planMappingById = new HashMap<>();
        if (!CollectionUtils.isEmpty(message.getPlanMappingList())) {
            for (CustPlanMapppingPojo planMapping : message.getPlanMappingList()) {
                if (planMapping.getId() != null) {
                    planMappingById.put(planMapping.getId(), planMapping);
                }
            }
        }

        for (CustPlanMapppingPojo packageMapping : packageMappings) {
            CustPlanMapppingPojo planMapping = planMappingById.get(packageMapping.getId());
            if (planMapping != null) {
                enrichPackageMapping(packageMapping, planMapping);
            }
        }
        if (CollectionUtils.isEmpty(packageMappings)
                && message.getCustPlanMappping() != null
                && message.getCustPlanMappping().getId() != null) {
            return Collections.singletonList(message.getCustPlanMappping());
        }
        if (CollectionUtils.isEmpty(packageMappings)) {
            return Collections.emptyList();
        }
        return packageMappings;
    }

    private void enrichPackageMapping(CustPlanMapppingPojo packageMapping, CustPlanMapppingPojo planMapping) {
        if (packageMapping.getQospolicyId() == null) packageMapping.setQospolicyId(planMapping.getQospolicyId());
        if (packageMapping.getOfferPrice() == null) packageMapping.setOfferPrice(planMapping.getOfferPrice());
        if (packageMapping.getTaxAmount() == null) packageMapping.setTaxAmount(planMapping.getTaxAmount());
        if (packageMapping.getDebitdocid() == null) packageMapping.setDebitdocid(planMapping.getDebitdocid());
        if (packageMapping.getWalletBalUsed() == null) packageMapping.setWalletBalUsed(planMapping.getWalletBalUsed());
        if (packageMapping.getPurchaseType() == null) packageMapping.setPurchaseType(planMapping.getPurchaseType());
        if (packageMapping.getPurchaseFrom() == null) packageMapping.setPurchaseFrom(planMapping.getPurchaseFrom());
        if (packageMapping.getGraceDays() == null) packageMapping.setGraceDays(planMapping.getGraceDays());
        if (packageMapping.getServiceId() == null) packageMapping.setServiceId(planMapping.getServiceId());
        if (packageMapping.getIsHold() == null) packageMapping.setIsHold(planMapping.getIsHold());
        if (packageMapping.getCustomerCpr() == null) packageMapping.setCustomerCpr(planMapping.getCustomerCpr());
        if (CollectionUtils.isEmpty(packageMapping.getQuotaList())) packageMapping.setQuotaList(planMapping.getQuotaList());
    }

    private Integer resolveCustPackageId(CustPlanMapppingPojo planMapping, CustQuotaDtlsPojo quotaDetails) {
        if (planMapping != null && planMapping.getId() != null) {
            return planMapping.getId();
        }
        if (quotaDetails != null && quotaDetails.getCprId() != null) {
            return quotaDetails.getCprId();
        }
        return null;
    }

    private List<CustSmsDetails> resolveSmsDetails(SaveCustomerDataShareMessage message) {
        List<CustSmsDetails> smsDetailsList = new ArrayList<>();
        if (message.getCustSmsDetailsList() != null) {
            smsDetailsList.addAll(message.getCustSmsDetailsList());
        }
        if (message.getCustSmsDetails() != null) {
            smsDetailsList.add(message.getCustSmsDetails());
        }
        return smsDetailsList;
    }

    private List<CustVoiceDetails> resolveVoiceDetails(SaveCustomerDataShareMessage message) {
        List<CustVoiceDetails> voiceDetailsList = new ArrayList<>();
        if (message.getCustVoiceDetailsList() != null) {
            voiceDetailsList.addAll(message.getCustVoiceDetailsList());
        }
        if (message.getCustVoiceDetails() != null) {
            voiceDetailsList.add(message.getCustVoiceDetails());
        }
        return voiceDetailsList;
    }

    private void saveCustomerQuotas(List<CustomerQuota> customerQuotaList) {
        if (customerQuotaList == null || customerQuotaList.isEmpty()) {
            return;
        }
        for (CustomerQuota customerQuota : customerQuotaList) {
            try {
                repository.saveQuota(customerQuota);
            } catch (ValidationException e) {
                logger.warn("Skipping duplicate quota id={} custId={} planId={} custPackageId={}: {}",
                        customerQuota.getQuotaDtlsId(),
                        customerQuota.getCustId(),
                        customerQuota.getPlanId(),
                        customerQuota.getCustPackageId(),
                        e.getMessage());
            }
        }
    }

    public CustPlanMappping convertDtoToDomainCustPlanMapping(CustPlanMapppingPojo custPlanMapppingPojo) {
        CustPlanMappping custPlanMappping = new CustPlanMappping();
        custPlanMappping.setCustPackageId(Long.valueOf(custPlanMapppingPojo.getId()));
        custPlanMappping.setCustId(custPlanMapppingPojo.getCustid());
        custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
        custPlanMappping.setService(custPlanMapppingPojo.getService());
        custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());
        custPlanMappping.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
        custPlanMappping.setStatus(custPlanMapppingPojo.getStatus());

        //TODO Arpit : Added missing fields
        custPlanMappping.setStartDate(custPlanMapppingPojo.getStartDate());
        custPlanMappping.setEndDate(custPlanMapppingPojo.getEndDate());
        custPlanMappping.setExpiryDate(custPlanMapppingPojo.getExpiryDate());
        custPlanMappping.setCreatedate(custPlanMapppingPojo.getCreatedate());
        custPlanMappping.setUpdatedate(custPlanMapppingPojo.getUpdatedate());
        custPlanMappping.setOfferPrice(custPlanMapppingPojo.getOfferPrice());
        custPlanMappping.setTaxAmount(custPlanMapppingPojo.getTaxAmount());
        custPlanMappping.setWalletBalUsed(custPlanMapppingPojo.getWalletBalUsed());
        custPlanMappping.setPurchaseType(custPlanMapppingPojo.getPurchaseType());
        custPlanMappping.setPurchaseFrom(custPlanMapppingPojo.getPurchaseFrom());
        if(custPlanMapppingPojo.getGraceDays() != null)
        {
            custPlanMappping.setGraceDays(custPlanMapppingPojo.getGraceDays().longValue());
        }
        custPlanMappping.setCreatedById(custPlanMapppingPojo.getCreatedById());
        custPlanMappping.setLastModifiedById(custPlanMapppingPojo.getLastModifiedById());
        custPlanMappping.setCreatedByName(custPlanMapppingPojo.getCreatedByName());
        custPlanMappping.setLastModifiedByName(custPlanMapppingPojo.getLastModifiedByName());
        custPlanMappping.setQosPolicyId(custPlanMapppingPojo.getQospolicyId());
        custPlanMappping.setDebitDocId(custPlanMapppingPojo.getDebitdocid());
        custPlanMappping.setServiceId(custPlanMapppingPojo.getServiceId());
        custPlanMappping.setIsHold(custPlanMapppingPojo.getIsHold());
        custPlanMappping.setCustomerCpr(custPlanMapppingPojo.getCustomerCpr());
        if(custPlanMapppingPojo.getCustServiceMappingId() != null)
        {
            custPlanMappping.setCustservicemappingid(custPlanMapppingPojo.getCustServiceMappingId().longValue());
        }
        if(custPlanMapppingPojo.getPlangroupid() != null)
        {
            custPlanMappping.setPlangroupid(custPlanMapppingPojo.getPlangroupid().longValue());
        }
        custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
        custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
        return custPlanMappping;
    }

    private Map<Long, Long> buildCustPackageIdByPlanId(List<CustPlanMappping> custPlanMapppings) {
        Map<Long, Long> custPackageIdByPlanId = new HashMap<>();
        if (custPlanMapppings == null) {
            return custPackageIdByPlanId;
        }
        for (CustPlanMappping custPlanMappping : custPlanMapppings) {
            if (custPlanMappping != null
                    && custPlanMappping.getPlanId() != null
                    && custPlanMappping.getCustPackageId() != null) {
                custPackageIdByPlanId.put(custPlanMappping.getPlanId(), custPlanMappping.getCustPackageId());
            }
        }
        return custPackageIdByPlanId;
    }

    private void attachCustPlanMapping(CustSmsDetails smsDetails, Map<Long, Long> custPackageIdByPlanId) {
        Long custPackageId = resolveCustPackageId(
                smsDetails.getCustPackageId(),
                smsDetails.getCustPlanMappping(),
                smsDetails.getPostpaidPlan() != null ? smsDetails.getPostpaidPlan().getId() : null,
                custPackageIdByPlanId);
        if (custPackageId == null) {
            logger.warn("SMS details id={} has no custpackageid for custid={} planid={}",
                    smsDetails.getId(),
                    smsDetails.getCustomer() != null ? smsDetails.getCustomer().getCustId() : null,
                    smsDetails.getPostpaidPlan() != null ? smsDetails.getPostpaidPlan().getId() : null);
            return;
        }
        smsDetails.setCustPlanMappping(custPlanMappingRepository.getReferenceById(custPackageId));
    }

    private void attachCustPlanMapping(CustVoiceDetails voiceDetails, Map<Long, Long> custPackageIdByPlanId) {
        Long custPackageId = resolveCustPackageId(
                voiceDetails.getCustPackageId(),
                voiceDetails.getCustPlanMappping(),
                voiceDetails.getPostpaidPlan() != null ? voiceDetails.getPostpaidPlan().getId() : null,
                custPackageIdByPlanId);
        if (custPackageId == null) {
            logger.warn("Voice details id={} has no custpackageid for custid={} planid={}",
                    voiceDetails.getId(),
                    voiceDetails.getCustomer() != null ? voiceDetails.getCustomer().getCustId() : null,
                    voiceDetails.getPostpaidPlan() != null ? voiceDetails.getPostpaidPlan().getId() : null);
            return;
        }
        voiceDetails.setCustPlanMappping(custPlanMappingRepository.getReferenceById(custPackageId));
    }

    private Long resolveCustPackageId(Long incomingCustPackageId,
                                      CustPlanMappping custPlanMappping,
                                      Integer planId,
                                      Map<Long, Long> custPackageIdByPlanId) {
        if (incomingCustPackageId != null) {
            return incomingCustPackageId;
        }
        if (custPlanMappping != null && custPlanMappping.getCustPackageId() != null) {
            return custPlanMappping.getCustPackageId();
        }
        if (planId != null) {
            return custPackageIdByPlanId.get(planId.longValue());
        }
        return null;
    }

public List<CustomerServiceMapping> addCustServiceMapping(
        List<CustomerServiceMapping> customerServiceMappings,
        Customer savedCustomer) {

    List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();

    for (CustomerServiceMapping serviceMapping : customerServiceMappings) {

        CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();

        customerServiceMapping.setId(serviceMapping.getId());
        customerServiceMapping.setServiceId(serviceMapping.getServiceId());
        customerServiceMapping.setCustId(serviceMapping.getCustId());
        customerServiceMapping.setConnectionNo(serviceMapping.getConnectionNo());
        customerServiceMapping.setPartnerId(serviceMapping.getPartnerId());
        customerServiceMapping.setPop(serviceMapping.getPop());
//      customerServiceMapping.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
        customerServiceMapping.setIsDeleted(serviceMapping.getIsDeleted());
        customerServiceMapping.setStatus(serviceMapping.getStatus());
        customerServiceMapping.setMvnoId(serviceMapping.getMvnoId());
        customerServiceMapping.setBuId(serviceMapping.getBuId());

        if (serviceMapping.getCreatedById() != null) {
            customerServiceMapping.setCreatedById(serviceMapping.getCreatedById());
        } else if (savedCustomer.getCreatedByStaffId() != null) {
            customerServiceMapping.setCreatedById(savedCustomer.getCreatedByStaffId());
        }

        if (serviceMapping.getLastModifiedById() != null) {
            customerServiceMapping.setLastModifiedById(serviceMapping.getLastModifiedById());
        } else if (savedCustomer.getLastModifiedByStaffId() != null) {
            customerServiceMapping.setLastModifiedById(savedCustomer.getLastModifiedByStaffId());
        }

        if (serviceMapping.getCreatedByName() != null) {
            customerServiceMapping.setCreatedByName(serviceMapping.getCreatedByName());
        } else if (savedCustomer.getCreateByName() != null) {
            customerServiceMapping.setCreatedByName(savedCustomer.getCreateByName());
        } else {
            customerServiceMapping.setCreatedByName("System");
        }

        if (serviceMapping.getLastModifiedByName() != null) {
            customerServiceMapping.setLastModifiedByName(serviceMapping.getLastModifiedByName());
        } else if (savedCustomer.getUpdateByName() != null) {
            customerServiceMapping.setLastModifiedByName(savedCustomer.getUpdateByName());
        } else {
            customerServiceMapping.setLastModifiedByName("System");
        }

        customerServiceMapping.setImsi(serviceMapping.getImsi());
        customerServiceMapping.setMsisdn(serviceMapping.getMsisdn());
        customerServiceMapping.setUsername(serviceMapping.getUsername());
        customerServiceMapping.setPassword(serviceMapping.getPassword());

        customerServiceMappingList.add(customerServiceMapping);
    }

    return customerServiceMappingList;
}

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }

    @Transactional
    public void updateCustomers(UpdateCustomerShareDataMessage message)
            throws ValidationException {
        try {

            Customer customer = repository
                    .findByCustId(BigInteger.valueOf(message.getId()))
                    .orElse(null);

            if (customer != null) {

                if (message.getId() != null) {
                    customer.setCustId(BigInteger.valueOf(message.getId()));
                }

                customer.setTitle(message.getTitle());
                customer.setUserName(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstName(message.getFirstname());
                customer.setLastName(message.getLastname());
                customer.setCustName(message.getCustname());

                if (message.getNetworkdevicesId() != null) {
                    customer.setNetworkDeviceId(
                            BigInteger.valueOf(message.getNetworkdevicesId())
                    );
                }

                customer.setCStatus(message.getStatus());
                customer.setCustomerType(message.getCusttype());

                if (message.getMvnoId() != null) {
                    customer.setMvnoId(BigInteger.valueOf(message.getMvnoId()));
                }

                if (message.getBuId() != null) {
                    customer.setBuid(BigInteger.valueOf(message.getBuId()));
                }

                customer.setIsDeleted(
                        message.getIsDeleted() != null
                                ? (short) (message.getIsDeleted() ? 1 : 0)
                                : null
                );

                customer.setFramedIpBind(message.getFramedIpBind());

                if (message.getOltportid() != null) {
                    customer.setOltPortId(BigInteger.valueOf(message.getOltportid()));
                }

                if (message.getOltslotid() != null) {
                    customer.setOltSlotId(BigInteger.valueOf(message.getOltslotid()));
                }

                customer.setNasPort(message.getNasPort());
                customer.setIpPoolNameBind(message.getIpPoolNameBind());
                customer.setFramedIp(message.getFramedIp());

                if (message.getParnterId() != null) {
                    customer.setPartnerId(BigInteger.valueOf(message.getParnterId()));
                }

                customer.setParentCustId(message.getParentCustId());
                customer.setCreatedByStaffId(message.getCreatedById());
                customer.setLastModifiedByStaffId(message.getLastModifiedById());
                customer.setBlockNo(message.getBlockNo());

                if(message.getServiceAreaId() != null)
                {
                    ServiceArea serviceArea =
                            serviceAreaRepository.findById(Long.valueOf(message.getServiceAreaId()))
                                    .orElse(null);
                    if (serviceArea != null && serviceArea.getId() != null) {
                        customer.setServiceAreaId(
                                BigInteger.valueOf(serviceArea.getId())
                        );
                    }
                }

                //Added missing fields
                customer.setFailCount(message.getFailCount());
                if(message.getWalletBalance() != null)
                {
                    customer.setWalletBalance(BigDecimal.valueOf(message.getWalletBalance()));
                }
                customer.setCreateDate(new Timestamp(System.currentTimeMillis()));
                if(message.getExpiryDate() != null)
                {
                    customer.setExpiryDate(String.valueOf(message.getExpiryDate()));
                }
                customer.setLastStatusChangeDate(parseDateTime(message.getLastStatusChangeDate()));
                customer.setAddParam1(message.getAddParam1());
                customer.setAddParam2(message.getAddParam2());
                customer.setAddParam3(message.getAddParam3());
                customer.setAddParam4(message.getAddParam4());
                customer.setSelfCarePwd(message.getSelfCarePwd());
                customer.setGst(message.getGst());
                customer.setAadhar(message.getAadhar());
                customer.setMacTelFlag(
                        message.getMactelFlag() != null
                                ? (short) (message.getMactelFlag() ? 1 : 0)
                                : null
                );
//            customer.setNextCafApprover(message.getne);
                customer.setServiceType(message.getServiceType());
                customer.setVoiceProvision(
                        message.getVoiceProvision() != null
                                ? (short) (message.getVoiceProvision() ? 1 : 0)
                                : null
                );
                customer.setVoipEnableFlag(
                        message.getVoipEnableFlag() != null
                                ? (short) (message.getVoipEnableFlag() ? 1 : 0)
                                : null
                );
                customer.setOnlineRenewalFlag(
                        message.getOnlineRenewalFlag() != null
                                ? (short) (message.getOnlineRenewalFlag() ? 1 : 0)
                                : null
                );
                customer.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                if(message.getWalletBalance() != null)
                {
                    customer.setWalletBalance(BigDecimal.valueOf(message.getWalletBalance()));
                }
                customer.setDidNo(message.getDidNo());
                customer.setVoiceSrvType(message.getVoiceSrvType());
                customer.setAltMobile(message.getAltMobile());
                if(message.getMaxConcurrentSession() != null)
                {
                    customer.setMaxConcurrentSession(BigInteger.valueOf(message.getMaxConcurrentSession()));
                }
                customer.setMacProvision(
                        message.getMacProvision() != null
                                ? (short) (message.getMacProvision() ? 1 : 0)
                                : null
                );
                customer.setMacAuthEnable(
                        message.getMacAuthEnable() != null
                                ? (short) (message.getMacAuthEnable() ? 1 : 0)
                                : null
                );
                customer.setNextQuotaResetDate(parseDate( message.getNextQuotaResetDate()));
                if(message.getMacRetentionPeriod() != null)
                {
                    customer.setMacRetentionPeriod(BigInteger.valueOf(message.getMacRetentionPeriod()));
                }
                customer.setMacRetentionUnit(message.getMacRetentionUnit());
                customer.setIsinvoicestop(message.getIsinvoicestop());
                Customer savedCustomer = repository.updateCustomer(customer);

                List<CustPlanMappping> custPlanMapppingList =
                        addCustPlanMapping(message.getCustPlanMapppingList(), savedCustomer);

                List<CustomerServiceMapping> customerServiceMappingList =
                        addCustServiceMapping(message.getCustomerServiceMappingList(), savedCustomer);

                custPlanMappingRepository.saveAll(custPlanMapppingList);
                customerServiceMappingRepository.saveAll(customerServiceMappingList);

                logger.info("Customer updated successfully with name {}", message.getUsername());

            } else {

                // If not found Ã¢â€ â€™ convert and call saveCustomers
                SaveCustomerDataShareMessage saveMessage = new SaveCustomerDataShareMessage();

                saveMessage.setId(message.getId());
                saveMessage.setTitle(message.getTitle());
                saveMessage.setUsername(message.getUsername());
                saveMessage.setPassword(message.getPassword());
                saveMessage.setFirstname(message.getFirstname());
                saveMessage.setLastname(message.getLastname());
                saveMessage.setCustname(message.getCustname());
                if(message.getServiceAreaId() != null)
                {
                    saveMessage.setServiceAreaId(message.getServiceAreaId());
                }
                saveMessage.setNetworkdevicesId(message.getNetworkdevicesId());
                saveMessage.setStatus(message.getStatus());
                saveMessage.setCusttype(message.getCusttype());
                saveMessage.setMvnoId(message.getMvnoId());
                saveMessage.setBuId(message.getBuId());
                saveMessage.setIsDeleted(message.getIsDeleted());
                saveMessage.setOltslotid(message.getOltslotid());
                saveMessage.setOltportid(message.getOltportid());
                saveMessage.setFullName(message.getFullName());
                saveMessage.setParnterId(message.getParnterId());
                saveMessage.setParentCustId(message.getParentCustId());
                saveMessage.setPopId(message.getPopId());
                saveMessage.setOltId(message.getOltId());
                saveMessage.setMasterdbid(message.getMasterdbid());
                saveMessage.setSplitterid(message.getSplitterid());
                saveMessage.setFramedIp(message.getFramedIp());
                saveMessage.setIpPoolNameBind(message.getIpPoolNameBind());
                saveMessage.setNasPort(message.getNasPort());
                saveMessage.setCustPlanMapppingList(message.getCustPlanMapppingList());
                saveMessage.setPlanMappingList(message.getCustPlanMapppingList());
                saveMessage.setCustomerServiceMappingList(message.getCustomerServiceMappingList());
                saveMessage.setCreatedById(message.getCreatedById());
                saveMessage.setLastModifiedById(message.getLastModifiedById());
                saveMessage.setFramedIpBind(message.getFramedIpBind());
                saveMessage.setBlockNo(message.getBlockNo());
                //Added missing fields
                saveMessage.setFailCount(message.getFailCount());
                if(message.getCreateDate() != null)
                {
                    saveMessage.setCreateDate(parseDateTime(message.getCreateDate()));
                }
                if(message.getExpiryDate() != null)
                {
                    saveMessage.setExpiryDate(parseDate(message.getExpiryDate()));
                }
                saveMessage.setLastStatusChangeDate(parseDateTime(message.getLastStatusChangeDate()));
                saveMessage.setAddParam1(message.getAddParam1());
                saveMessage.setAddParam2(message.getAddParam2());
                saveMessage.setAddParam3(message.getAddParam3());
                saveMessage.setAddParam4(message.getAddParam4());
                saveMessage.setSelfCarePwd(message.getSelfCarePwd());
                saveMessage.setGst(message.getGst());
                saveMessage.setAadhar(message.getAadhar());
                saveMessage.setMactelFlag(message.getMactelFlag());
                saveMessage.setServiceType(message.getServiceType());
                saveMessage.setVoiceProvision(message.getVoiceProvision());
                saveMessage.setVoipEnableFlag(message.getVoipEnableFlag());
                saveMessage.setOnlineRenewalFlag(message.getOnlineRenewalFlag());
                if(message.getUpdateDate() != null)
                {
                    saveMessage.setUpdateDate(parseDateTime(message.getUpdateDate()));
                }
                if(message.getWalletBalance() != null)
                {
                    saveMessage.setWalletBalance(message.getWalletBalance());
                }
                saveMessage.setDidNo(message.getDidNo());
                saveMessage.setVoiceSrvType(message.getVoiceSrvType());
                saveMessage.setAltMobile(message.getAltMobile());
                if(message.getMaxConcurrentSession() != null)
                {
                    saveMessage.setMaxConcurrentSession(message.getMaxConcurrentSession());
                }
                saveMessage.setMacProvision(message.getMacProvision());
                saveMessage.setMacAuthEnable(message.getMacAuthEnable());
                saveMessage.setNextQuotaResetDate(parseDate(message.getNextQuotaResetDate()));
                if(message.getMacRetentionPeriod() != null)
                {
                    saveMessage.setMacRetentionPeriod(message.getMacRetentionPeriod());
                }
                saveMessage.setMacRetentionUnit(message.getMacRetentionUnit());
                saveMessage.setWalletBalance(message.getWalletBalance());
                saveCustomers(saveMessage);
            }

        } finally {
            logger.info("Customer update processing completed for username={}", message.getUsername());
        }
    }


    public static LocalDateTime parseDateTime(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            // Default ISO format
            return LocalDateTime.parse(date);
        } catch (Exception e) {
            // Flexible formatter for millis/nanos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SSSSSSSSS]"
            );
            return LocalDateTime.parse(date, formatter);
        }
    }

    private LocalDateTime getDateTime(LocalDateTime dateTime, String dateTimeString) {
        return dateTime != null ? dateTime : parseDateTime(dateTimeString);
    }

    public static LocalDate parseDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(date); // yyyy-MM-dd
        } catch (Exception e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd['T'HH:mm:ss]"
            );
            return LocalDate.parse(date, formatter);
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            return null;
        }
        return Long.valueOf(stringValue);
    }
    public Timestamp parseTimestamp(String date) {
        if (date == null) return null;
        return Timestamp.valueOf(date.replace("T", " "));
    }

    public void updateCustomerStatus(CustomerUpdateMessage message) {
        try {
            Map<String, Object> customerData = message.getCustomerData();
            String idString = customerData.get("id").toString();
            int id = (int) Double.parseDouble(idString);

            Customer customer = repository
                    .findByCustId(BigInteger.valueOf(id))
                    .orElse(null);

            if (customer != null) {
                customer.setCStatus(customerData.get("status").toString());
                repository.updateCustomer(customer);
                logger.info("Customer status updated successfully with id {}",id);
            } else {
                logger.warn("Customer not found with id {} for status update", id);
            }
        } catch (Throwable e) {
            logger.error("Unable to update customer status with id {} , Error: {}",
                    message.getCustomerData().get("id").toString() ,
                    e.getMessage());
        }
    }
    public void updateServiceStatus(CustomerServiceActivationMessage message) {
        try {
            CustomerServiceMapping findByCustIdAndId = customerServiceMappingRepository.findByCustIdAndCustMappingId((message.getCustId()), (message.getId()));
            if (findByCustIdAndId != null) {
                findByCustIdAndId.setStatus(message.getStatus());
                customerServiceMappingRepository.save(findByCustIdAndId);
                logger.info("Service status updated successfully with id {}",message.getId());
            } else {
                logger.warn("Service not found with id {} for status update",message.getId());
            }
            logger.info("Service status updated successfully for customer id {} and service mapping id {} ",message.getCustId(), message.getId());
        } catch (Throwable e) {
            logger.error("Unable to update service status with id {} , Error: {}",
                    message.getId() ,
                    e.getMessage());
        }
    }

    @Transactional
    public void updateCustomerPackageRel(CustomerPackageRelMessage message) {
        try {
            Map<String, Object> data = message.getData();
            if (data == null || data.get("id") == null || data.get("custid") == null) {
                logger.warn("Received empty data or missing mandatory identifiers in CustomerPackageRelMessage");
                return;
            }
            BigInteger custPackageId = new BigInteger(String.valueOf(((Number) data.get("id")).longValue()));
            BigInteger custId = new BigInteger(String.valueOf(((Number) data.get("custid")).longValue()));

            Timestamp endDate = null;
            if (data.get("endDate") != null) {
                endDate = Timestamp.valueOf(data.get("endDate").toString());
            }

            Timestamp expiryDate = null;
            if (data.get("expiryDate") != null) {
                expiryDate = Timestamp.valueOf(data.get("expiryDate").toString());
            }

            String status = data.get("custPlanStatus") != null ? data.get("custPlanStatus").toString() : null;
            if(endDate!= null && expiryDate != null ) {
                repository.updateCustomerPackageRel(endDate, expiryDate, custPackageId, custId);
                logger.info("Successfully updated Customer Package Rel from Kafka for messageId: {}", message.getMessageId());
            }
            else {
                logger.warn("End date or expiry date is null for Customer Package Rel update, skipping update for messageId: {}", message.getMessageId());
            }
            saveQuotaDetails(data, custId, custPackageId);
            saveSmsAndVoiceDetails(data);
        } finally {
            logger.info("Customer Package Rel processing completed for messageId={}", message.getMessageId());
        }
    }

    private void saveQuotaDetails(Map<String, Object> data, BigInteger custId, BigInteger custPackageId) {
        Object quotaDetailsObject = data.get("quotaDtls");
        if (!(quotaDetailsObject instanceof List)) {
            return;
        }
        Long parentPlanId = toLong(data.get("planId"));
        for (Object quotaObject : (List<?>) quotaDetailsObject) {
            if (!(quotaObject instanceof Map)) {
                continue;
            }
            Map<?, ?> quotaMap = (Map<?, ?>) quotaObject;
            Long quotaId = toLong(quotaMap.get("id"));
            Long planId = toLong(quotaMap.get("planId"));
            if (planId == null) {
                planId = parentPlanId;
            }
            if (quotaId == null || planId == null || custId == null || custPackageId == null) {
                continue;
            }

            CustomerQuota customerQuota = new CustomerQuota();
            customerQuota.setQuotaDtlsId(BigInteger.valueOf(quotaId));
            customerQuota.setCustId(custId);
            customerQuota.setPlanId(BigInteger.valueOf(planId));
            customerQuota.setQuotaType(defaultString(quotaMap.get("quotaType")));
            customerQuota.setTotalQuota(toBigDecimalFromObject(quotaMap.get("totalQuota")));
            customerQuota.setUsedQuota(toBigDecimalFromObject(quotaMap.get("usedQuota")));
            customerQuota.setQuotaUnit(defaultString(quotaMap.get("quotaUnit")));
            customerQuota.setTimeTotalQuota(toBigDecimalFromObject(quotaMap.get("timeTotalQuota")));
            customerQuota.setTimeQuotaUsed(toBigDecimalFromObject(quotaMap.get("timeQuotaUsed")));
            customerQuota.setTimeQuotaUnit(toStringValue(quotaMap.get("timeQuotaUnit")));
            customerQuota.setIsDeleted(toBooleanValue(quotaMap.get("isDelete"), Boolean.FALSE));
            customerQuota.setTotalQuotaKb(toBigDecimalFromObject(quotaMap.get("totalQuotaKB")));
            customerQuota.setUsedQuotaKb(toBigDecimalFromObject(quotaMap.get("usedQuotaKB")));
            customerQuota.setTimeTotalQuotaSec(toBigDecimalFromObject(quotaMap.get("timeTotalQuotaSec")));
            customerQuota.setTimeUsedQuotaSec(toBigDecimalFromObject(quotaMap.get("timeUsedQuotaSec")));
            customerQuota.setCustPackageId(custPackageId.intValue());
            customerQuota.setDidTotalQuota(toBigDecimalFromObject(quotaMap.get("didtotalquota")));
            customerQuota.setDidUsedQuota(toBigDecimalFromObject(quotaMap.get("didusedquota")));
            customerQuota.setIntercomTotalQuota(toBigDecimalFromObject(quotaMap.get("intercomtotalquota")));
            customerQuota.setIntercomUsedQuota(toBigDecimalFromObject(quotaMap.get("intercomusedquota")));
            customerQuota.setDidQuotaUnit(toStringValue(quotaMap.get("didQuotaUnit")));
            customerQuota.setIntercomQuotaUnit(toStringValue(quotaMap.get("intercomQuotaUnit")));
            customerQuota.setCreatedByStaffId(toBigDecimalNullable(quotaMap.get("createdByStaffId")));
            customerQuota.setLastModifiedByStaffId(toBigDecimalNullable(quotaMap.get("lastModifiedByStaffId")));
            customerQuota.setCreateByName(toStringValue(quotaMap.get("createdByName")));
            customerQuota.setUpdateByName(toStringValue(quotaMap.get("updatedByName")));
            customerQuota.setSpeedDowngradeFlag(Boolean.FALSE);
            customerQuota.setIsFupApplied(Boolean.FALSE);
            customerQuota.setCurrentSessionUsageTime(toBigDecimalFromObject(quotaMap.get("currentSessionUsageTime")));
            customerQuota.setCurrentSessionUsageVolume(toBigDecimalFromObject(quotaMap.get("currentSessionUsageVolume")));
            customerQuota.setParentQuotaType(toStringValue(quotaMap.get("parentQuotaType")));
            customerQuota.setIsChunkAvailable(toBooleanValue(quotaMap.get("isChunkAvailable"), Boolean.FALSE));
            customerQuota.setReservedQuotaInPer(toDoubleValue(quotaMap.get("reservedQuotaInPer")));
            customerQuota.setTotalReservedQuota(toDoubleValue(quotaMap.get("totalReservedQuota")));
            customerQuota.setUsageQuotaType(toStringValue(quotaMap.get("usageQuotaType")));
            customerQuota.setSkipQuotaUpdate(toBooleanValue(quotaMap.get("skipQuotaUpdate"), Boolean.FALSE));
            customerQuota.setIsQuotaUpdateSkipped(Boolean.FALSE);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            customerQuota.setCreateDate(now);
            customerQuota.setLastModifiedDate(now);

            try {
                if (repository.quotaExists(custId, BigInteger.valueOf(planId), custPackageId.intValue())) {
                    logger.warn("Skipping duplicate quota for custId={}, planId={}, custPackageId={}", custId, planId, custPackageId);
                    continue;
                }
                repository.saveQuota(customerQuota);
            } catch (ValidationException e) {
                logger.warn("Skipping duplicate quota for custId={}, planId={}, custPackageId={}", custId, planId, custPackageId);
            } catch (Exception e) {
                logger.error("Unable to save quota for custId={}, planId={}, custPackageId={}: {}", custId, planId, custPackageId, e.getMessage(), e);
            }
        }
    }

    private void saveSmsAndVoiceDetails(Map<String, Object> data) {
        saveSmsDetails(data.get("custSmsDtls"));
        saveVoiceDetails(data.get("custVoiceDtls"));
    }

    private void saveChangePlanQuotaDetails(Object quotaDetailsObject) {
        if (!(quotaDetailsObject instanceof List)) {
            return;
        }
        for (Object quotaObject : (List<?>) quotaDetailsObject) {
            if (!(quotaObject instanceof Map)) {
                continue;
            }
            Map<?, ?> quotaMap = (Map<?, ?>) quotaObject;
            Long custId = toLong(firstNonNull(quotaMap.get("custid"), quotaMap.get("custId")));
            Long custPackageId = toLong(firstNonNull(quotaMap.get("custpackageid"), quotaMap.get("custPackageId")));
            Long planId = toLong(firstNonNull(quotaMap.get("planId"), quotaMap.get("planid")));
            if (custId == null || custPackageId == null) {
                logger.warn("Skipping ChangePlan quota without custId/custPackageId. quota={}", quotaMap);
                continue;
            }
            Map<String, Object> packageData = new HashMap<>();
            packageData.put("quotaDtls", Collections.singletonList(quotaMap));
            packageData.put("planId", planId);
            saveQuotaDetails(packageData, BigInteger.valueOf(custId), BigInteger.valueOf(custPackageId));
        }
    }
    private void saveSmsDetails(Object smsDetailsObject) {
        if (!(smsDetailsObject instanceof List)) {
            return;
        }
        List<CustSmsDetails> smsDetailsList = new ArrayList<>();
        for (Object smsObject : (List<?>) smsDetailsObject) {
            if (!(smsObject instanceof Map)) {
                continue;
            }
            Map<?, ?> smsMap = (Map<?, ?>) smsObject;
            Long smsId = toLong(smsMap.get("smsdtlsid"));
            Long custId = toLong(smsMap.get("custid"));
            Long planId = toLong(firstNonNull(smsMap.get("planId"), smsMap.get("planid")));
            Long custPackageId = toLong(firstNonNull(smsMap.get("custpackageid"), smsMap.get("custPackageId")));
            if (smsId == null || custId == null || planId == null) {
                continue;
            }
            CustSmsDetails smsDetails = new CustSmsDetails();
            smsDetails.setId(smsId);
            Customer customer = new Customer();
            customer.setCustId(BigInteger.valueOf(custId));
            smsDetails.setCustomer(customer);
            PostpaidPlan postpaidPlan = new PostpaidPlan();
            postpaidPlan.setId(planId.intValue());
            smsDetails.setPostpaidPlan(postpaidPlan);
            if (custPackageId != null) {
                custPlanMappingRepository.findById(custPackageId).ifPresent(smsDetails::setCustPlanMappping);
            }
            smsDetails.setSmsType(toStringValue(smsMap.get("smstype")));
            smsDetails.setTotalSms(toBigDecimalFromObject(smsMap.get("totalsms")));
            smsDetails.setUsedSms(toBigDecimalFromObject(smsMap.get("usedsms")));
            smsDetailsList.add(smsDetails);
        }
        if (!smsDetailsList.isEmpty()) {
            try {
                custSmsDetailsRepository.saveAllAndFlush(smsDetailsList);
            } catch (Exception e) {
                logger.error("Unable to save Diameter SMS details during sync: {}", e.getMessage(), e);
            }
        }
    }

    private void saveVoiceDetails(Object voiceDetailsObject) {
        if (!(voiceDetailsObject instanceof List)) {
            return;
        }
        List<CustVoiceDetails> voiceDetailsList = new ArrayList<>();
        for (Object voiceObject : (List<?>) voiceDetailsObject) {
            if (!(voiceObject instanceof Map)) {
                continue;
            }
            Map<?, ?> voiceMap = (Map<?, ?>) voiceObject;
            Long voiceId = toLong(voiceMap.get("voicedtlsid"));
            Long custId = toLong(voiceMap.get("custid"));
            Long planId = toLong(firstNonNull(voiceMap.get("planId"), voiceMap.get("planid")));
            Long custPackageId = toLong(firstNonNull(voiceMap.get("custpackageid"), voiceMap.get("custPackageId")));
            if (voiceId == null || custId == null || planId == null) {
                continue;
            }
            CustVoiceDetails voiceDetails = new CustVoiceDetails();
            voiceDetails.setId(voiceId);
            Customer customer = new Customer();
            customer.setCustId(BigInteger.valueOf(custId));
            voiceDetails.setCustomer(customer);
            PostpaidPlan postpaidPlan = new PostpaidPlan();
            postpaidPlan.setId(planId.intValue());
            voiceDetails.setPostpaidPlan(postpaidPlan);
            if (custPackageId != null) {
                custPlanMappingRepository.findById(custPackageId).ifPresent(voiceDetails::setCustPlanMappping);
            }
            voiceDetails.setVoiceType(toStringValue(voiceMap.get("voicetype")));
            voiceDetails.setTotalVoice(toBigDecimalFromObject(voiceMap.get("totalvoice")));
            voiceDetails.setUsedVoice(toBigDecimalFromObject(voiceMap.get("usedvoice")));
            voiceDetails.setPulse(toStringValue(voiceMap.get("pulse")));
            voiceDetails.setCreatedById(toIntegerValue(voiceMap.get("createdByStaffId")));
            voiceDetails.setLastModifiedById(toIntegerValue(voiceMap.get("lastModifiedByStaffId")));
            voiceDetails.setCreatedByName(toStringValue(voiceMap.get("createdByName")));
            voiceDetails.setLastModifiedByName(toStringValue(voiceMap.get("updatedByName")));
            voiceDetailsList.add(voiceDetails);
        }
        if (!voiceDetailsList.isEmpty()) {
            try {
                custVoiceDetailsRepository.saveAllAndFlush(voiceDetailsList);
            } catch (Exception e) {
                logger.error("Unable to save Diameter voice details during sync: {}", e.getMessage(), e);
            }
        }
    }

    private Object firstNonNull(Object first, Object second) {
        return first != null ? first : second;
    }

    private Integer toIntegerValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(value.toString());
    }
    private String toStringValue(Object value) {
        return value != null ? value.toString() : null;
    }

    private String defaultString(Object value) {
        return value != null ? value.toString() : "";
    }

    private BigDecimal toBigDecimalNullable(Object value) {
        if (value == null) {
            return null;
        }
        return toBigDecimalFromObject(value);
    }

    private BigDecimal toBigDecimalFromObject(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(stringValue);
    }

    private Boolean toBooleanValue(Object value, Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.valueOf(value.toString());
    }

    private Double toDoubleValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            return null;
        }
        return Double.valueOf(stringValue);
    }

    @Transactional
    public void saveCustomersPlanAndServiceData(ChangePlanMessage message) {

        logger.info("[Diameter] Processing ChangePlanMessage | type={} renewalId={}",
                message.getType(), message.getRenewalId());
        Integer iCustId =0;
        if (!CollectionUtils.isEmpty(message.getNewCustPlanMappingRevenues())) {
            for (CustPlanMappingRevenue dto : message.getNewCustPlanMappingRevenues()) {
                try {
                	iCustId=dto.getCustomerId();
                    saveCustPlanMapping(dto);
                } finally {
                    logger.info("[Diameter] CustPlanMappping processing completed for id={}", dto.getId());
                }
            }
        }
        if (!CollectionUtils.isEmpty(message.getOldCustPlanMappingRevenues())) {
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (CustPlanMappingRevenue data : message.getOldCustPlanMappingRevenues()) {
                Optional<CustPlanMappping> custPlanMappping1 = custPlanMappingRepository.findById(data.getId().longValue());
                if (custPlanMappping1.isPresent()) {
                    CustPlanMappping custPlanMappping = custPlanMappping1.get();
                    if (data.getStartDate() != null) {
                        custPlanMappping.setStartDate(parseDatetime(data.getStartDate()));
                    }
                    if (data.getEndDate() != null) {
                        custPlanMappping.setEndDate(parseDatetime(data.getEndDate()));
                    }
                    if (data.getExpiryDate() != null) {
                        custPlanMappping.setExpiryDate(parseDatetime(data.getExpiryDate()));
                    }
                    custPlanMappping.setCustPlanStatus(data.getCustPlanStatus());
                    custPlanMapppings.add(custPlanMappping);
                    ids.add(data.getId());
                } else {
                    logger.warn("Customer Plan Mapping not found with id {} for update", data.getId());
                }
            }
            custPlanMappingRepository.saveAll(custPlanMapppings);
        }

        if (!CollectionUtils.isEmpty(message.getCustomerServiceMappingRevenues())) {
            for (CustomerServiceMappingRevenue dto : message.getCustomerServiceMappingRevenues()) {
                try {
                    saveCustomerServiceMapping(dto);
                } finally {
                    logger.info("[Diameter] CustomerServiceMapping processing completed for id={}", dto.getId());
                }
            }
        }
        
        logger.info("iCustId is : {}", iCustId);
        if(iCustId > 0) {
        	logger.info("Calling sendRAR ");
        	sendRAR(BigDecimal.valueOf(Integer.valueOf(iCustId)));
        }

        logger.info("[Diameter] ChangePlanMessage processed | type={} renewalId={}",
                message.getType(), message.getRenewalId());
    }

    @Override
    @Transactional
    public void updateCustomerServiceMappingImsi(
            Integer customerId,
            String msisdn,
            String newImsi) {

        CustomerServiceMapping mapping = customerServiceMappingRepository.findByCustIdAndMsisdnAndIsDeletedFalse(customerId, msisdn)
                        .orElseThrow(() ->
                                new RuntimeException("Customer Service Mapping not found"));

        mapping.setImsi(newImsi);

        customerServiceMappingRepository.save(mapping);
    }

    private void saveCustPlanMapping(CustPlanMappingRevenue dto) {
        CustPlanMappping entity = custPlanMappingRepository.findById(dto.getId().longValue()).orElse(new CustPlanMappping());

        entity.setCustPackageId(dto.getId() != null ? dto.getId().longValue() : null);
        entity.setCustId(dto.getCustomerId() != null ? dto.getCustomerId().longValue() : null);
        entity.setPlanId(dto.getPlanId() != null ? dto.getPlanId().longValue() : null);
        entity.setService(dto.getService());
        entity.setStatus(dto.getStatus());
        entity.setCustPlanStatus(dto.getCustPlanStatus());
        entity.setOfferPrice(dto.getOfferPrice());
        entity.setTaxAmount(dto.getTaxAmount());
        entity.setWalletBalUsed(dto.getWalletBalUsed());
        entity.setPurchaseType(dto.getPurchaseType());
        entity.setOnlinePurchaseId(dto.getOnlinePurchaseId());
        entity.setPurchaseFrom(dto.getPurchaseFrom());
        entity.setIsDelete(dto.getIsDelete());
        entity.setDebitDocId(dto.getDebitdocid());
        entity.setCreditDocId(dto.getCreditdocid());
        entity.setCustservicemappingid(
                dto.getCustServiceMappingId() != null ? dto.getCustServiceMappingId().longValue() : null);
        entity.setPlangroupid(
                dto.getPlanGroupId() != null ? dto.getPlanGroupId().longValue() : null);
        entity.setIsInvoiceToOrg(dto.getIsInvoiceToOrg());
        entity.setBillTo(dto.getBillTo());
        entity.setIsHold(dto.getIsHold() != null ? dto.getIsHold() : false);
        entity.setServiceId(dto.getServiceId());

        if (dto.getStartDate() != null) {
            entity.setStartDate(parseDatetime(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(parseDatetime(dto.getEndDate()));
        }
        if (dto.getExpiryDate() != null) {
            entity.setExpiryDate(parseDatetime(dto.getExpiryDate()));
        }
        if(dto.getQosPolicyId() != null) {
            entity.setQosPolicyId(dto.getQosPolicyId());
        }
        custPlanMappingRepository.saveAndFlush(entity);
        logger.debug("[Diameter] Saved CustPlanMappping id={} custId={} planId={}",
                dto.getId(), dto.getCustomerId(), dto.getPlanId());
    }

    private void saveCustomerServiceMapping(CustomerServiceMappingRevenue dto) {
        if (dto.getId() == null) return;

        CustomerServiceMapping entity = customerServiceMappingRepository
                .findById(dto.getId())
                .orElse(new CustomerServiceMapping());

        entity.setId(dto.getId());
        entity.setCustId(dto.getCustId());
        entity.setServiceId(dto.getServiceId());
        entity.setConnectionNo(dto.getConnectionNo());
        entity.setMvnoId(dto.getMvnoId());
        entity.setBuId(dto.getBuId());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getMsisdn() != null) {
            entity.setMsisdn(dto.getMsisdn());
        }
        if (dto.getImsi() != null) {
            entity.setImsi(dto.getImsi());
        }
        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getCreatedByStaffId() != null) {
            entity.setCreatedById(dto.getCreatedByStaffId());
        }
        if (dto.getLastModifiedByStaffId() != null) {
            entity.setLastModifiedById(dto.getLastModifiedByStaffId());
        }
        if (dto.getCreatedByName() != null) {
            entity.setCreatedByName(dto.getCreatedByName());
        }
        if (dto.getUpdatedByName() != null) {
            entity.setLastModifiedByName(dto.getUpdatedByName());
        }
        if (dto.getIsDelete() != null) {
            entity.setIsDeleted(dto.getIsDelete());
        }

        customerServiceMappingRepository.saveAndFlush(entity);
        logger.debug("[Diameter] Saved CustomerServiceMapping id={} custId={} ",
                dto.getId(), dto.getCustId());
    }
    private LocalDateTime parseDatetime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr.length() == 16
                    ? dateStr + ":00"
                    : dateStr.substring(0, 19));
        } catch (Exception e) {
            logger.warn("[Diameter] Could not parse date string '{}': {}", dateStr, e.getMessage());
            return null;
        }
    }

    @Override
    public void saveDiameterChangePlanSyncData(ChangePlanMessage message, Map<String, Object> syncData) {
        logger.info("[Diameter] Processing DiameterChangePlanSyncMessage | type={} renewalId={}",
                message.getType(), message.getRenewalId());
        Integer customerId = 0;
        if (!CollectionUtils.isEmpty(message.getNewCustPlanMappingRevenues())) {
            for (CustPlanMappingRevenue dto : message.getNewCustPlanMappingRevenues()) {
                if (dto.getCustomerId() != null) {
                    customerId = dto.getCustomerId();
                }
                try {
                    saveCustPlanMapping(dto);
                } catch (Exception e) {
                    logger.error("Unable to save Diameter CustPlanMappping id={} custId={} planId={}: {}",
                            dto.getId(), dto.getCustomerId(), dto.getPlanId(), e.getMessage(), e);
                }
            }
        }
        try {
            saveCustomerServiceMappingDetails(syncData.get("customerServiceMappings"));
        } catch (Exception e) {
            logger.error("Unable to save Diameter customer service mappings during sync: {}", e.getMessage(), e);
        }
        saveChangePlanQuotaDetails(syncData.get("quotaDtls"));
        saveSmsDetails(syncData.get("custSmsDtls"));
        saveVoiceDetails(syncData.get("custVoiceDtls"));
        if (customerId > 0) {
            try {
                sendRAR(BigDecimal.valueOf(customerId));
            } catch (Exception e) {
                logger.warn("Unable to send Diameter RAR after sync for custId={}: {}", customerId, e.getMessage());
            }
        }
        logger.info("[Diameter] DiameterChangePlanSyncMessage processed | type={} renewalId={}",
                message.getType(), message.getRenewalId());
    }

    private void saveCustomerServiceMappingDetails(Object serviceMappingObject) {
        if (!(serviceMappingObject instanceof List)) {
            return;
        }
        for (Object mappingObject : (List<?>) serviceMappingObject) {
            if (!(mappingObject instanceof Map)) {
                continue;
            }
            Map<?, ?> serviceMap = (Map<?, ?>) mappingObject;
            Long id = toLong(serviceMap.get("id"));
            if (id == null) {
                continue;
            }
            CustomerServiceMapping entity = customerServiceMappingRepository
                    .findById(id.intValue())
                    .orElse(new CustomerServiceMapping());
            entity.setId(id.intValue());
            Long custId = toLong(firstNonNull(serviceMap.get("custId"), serviceMap.get("custid")));
            if (custId != null) {
                entity.setCustId(custId.intValue());
            }
            Long serviceId = toLong(serviceMap.get("serviceId"));
            if (serviceId != null) {
                entity.setServiceId(serviceId);
            }
            entity.setConnectionNo(toStringValue(serviceMap.get("connectionNo")));
            entity.setStatus(toStringValue(serviceMap.get("status")));
            entity.setMsisdn(toStringValue(serviceMap.get("msisdn")));
            entity.setImsi(toStringValue(serviceMap.get("imsi")));
            entity.setUsername(toStringValue(serviceMap.get("username")));
            entity.setPassword(toStringValue(serviceMap.get("password")));
            Long mvnoId = toLong(serviceMap.get("mvnoId"));
            if (mvnoId != null) {
                entity.setMvnoId(mvnoId.intValue());
            }
            Long buId = toLong(serviceMap.get("buId"));
            if (buId != null) {
                entity.setBuId(buId);
            }
            if (serviceMap.get("isDelete") != null) {
                entity.setIsDeleted(toBooleanValue(serviceMap.get("isDelete"), Boolean.FALSE));
            }
            entity.setCreatedById(toIntegerValue(serviceMap.get("createdByStaffId")));
            entity.setLastModifiedById(toIntegerValue(serviceMap.get("lastModifiedByStaffId")));
            entity.setCreatedByName(toStringValue(serviceMap.get("createdByName")));
            entity.setLastModifiedByName(toStringValue(serviceMap.get("updatedByName")));
            customerServiceMappingRepository.saveAndFlush(entity);
        }
    }
    @Override
    public void sendRAR(BigDecimal customerId) {
        DiameterUtils.sendRAR(customerId, cacheManagerServiceImpl, diameterStack, genericDiameterProcessor,mappingHeaderServiceImpl,this ,planServiceImpl,qosPolicyServiceImpl);
    }
}
