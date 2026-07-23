package com.diameter.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.Customer;
import com.diameter.model.CustomerPackageRel;
import com.diameter.model.CustomerQuota;
import com.diameter.model.PostpaidPlan;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepository.class);

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer saveCustomer(Customer customer) throws ValidationException {
        try {
            logger.debug("Creating customer with ID: {}", customer.getCustId());
            Integer userCount = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_USERNAME_EXISTS, Integer.class, customer.getUserName(), customer.getMvnoId());

            if (userCount != null && userCount > 0) {
                throw new ValidationException("Customer already exisits with given Username and MVNOID");
            }

            jdbcTemplate.update(DiameterSQLConstants.INSERT_CUSTOMER, customer.getCustId(), customer.getUserName(), customer.getPassword(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getCStatus(), customer.getLastLoginTime(), customer.getFailCount(), customer.getLastPasswordChange(), customer.getAccountNumber(), customer.getAccountType(), customer.getBirthDate(), customer.getCountry(), customer.getCui(), customer.getCustomerType(), customer.getGender(), customer.getImsi(), customer.getPhone(), customer.getSubscriberPackage(), customer.getSubscriberPackageId(), customer.getCreateDate(), customer.getExpiryDate(), customer.getLastStatusChangeDate(), customer.getNextBillDate(), customer.getLastBillDate(), customer.getBillDay(), customer.getOutstandingBalance(), customer.getPartnerId(), customer.getAsnNumber(), customer.getBngRouterInterface(), customer.getBngRouterName(), customer.getIpPrefixes(), customer.getIpv6Prefixes(), customer.getLanIp(), customer.getLanIpv6(), customer.getLlAccountId(), customer.getLlConnectionType(), customer.getLlExpiryDate(), customer.getLlMedium(), customer.getLlServiceId(), customer.getMacAddress(), customer.getPeerIp(), customer.getPoolIp(), customer.getQos(), customer.getRdExport(), customer.getRdValue(), customer.getVlanId(), customer.getVrfName(), customer.getVsiId(), customer.getVsiName(), customer.getWanIp(), customer.getWanIpv6(), customer.getBillEntityName(), customer.getPurchaseOrder(), customer.getRemarks(), customer.getAddParam1(), customer.getAddParam2(), customer.getAddParam3(), customer.getAddParam4(), customer.getParentCustId(), customer.getInvoiceOption(), customer.getOldPassword1(), customer.getOldPassword2(), customer.getOldPassword3(), customer.getFirstActivationDate(), customer.getAllowedIpAddrs(), customer.getSelfCarePwd(), customer.getTitle(), customer.getCustName(), customer.getContactPerson(), customer.getCafNo(), customer.getPan(), customer.getGst(), customer.getAadhar(), customer.getMacTelFlag(), customer.getMobile(), customer.getAltMobile(), customer.getAltPhone(), customer.getAltEmail(), customer.getFax(), customer.getResellerId(), customer.getSalesRepId(), customer.getDeposit(), customer.getVoiceSrvType(), customer.getDidNo(), customer.getChildDidNo(), customer.getIntercomNo(), customer.getIntercomGrp(), customer.getOnlineRenewalFlag(), customer.getVoipEnableFlag(), customer.getCustCategory(), customer.getWalletBalance(), customer.getNetworkType(), customer.getOltSlotId(), customer.getOltPortId(), customer.getStrConnType(), customer.getStrOltName(), customer.getStrSlotName(), customer.getStrPortName(), customer.getCreatedByStaffId(), customer.getLastModifiedByStaffId(), customer.getServiceAreaId(), customer.getNetworkDeviceId(), customer.getOnuId(), customer.getIsDeleted(), customer.getDefaultPoolId(), customer.getOtp(), customer.getOtpValidate(), customer.getCreateByName(), customer.getUpdateByName(), customer.getLastModifiedDate(), customer.getLatitude(), customer.getLongitude(), customer.getUrl(), customer.getGisCode(), customer.getVoiceProvision(), customer.getSalesRemark(), customer.getServiceType(), customer.getPreviousCafApprover(), customer.getNextCafApprover(), customer.getCafApproveStatus(), customer.getMvnoId(), customer.getCalendarType(), customer.getInvoiceType(), customer.getNasPort(), customer.getFramedIp(), customer.getFramedIpBind(), customer.getIpPoolNameBind(), customer.getBuid(), customer.getMaxConcurrentSession(), customer.getGatewayIp(), customer.getSkipNetConf(), customer.getRdImport(), customer.getMvnoDeactivationFlag(), customer.getIpv4(), customer.getIpv6(), customer.getVlan(), customer.getNasPortId(), customer.getNasIpAddress(), customer.getFramedIpv6Address(), customer.getDelegatedPrefix(), customer.getFramedRoute(), customer.getMacProvision(), customer.getMacAuthEnable(), customer.getFramedIpNetmask(), customer.getFramedIpv6Prefix(), customer.getPrimaryDns(), customer.getPrimaryIpv6Dns(), customer.getSecondaryIpv6Dns(), customer.getSecondaryDns(), customer.getMacRetentionPeriod(), customer.getMacRetentionUnit(), customer.getNextQuotaResetDate(), customer.getBlockNo(),customer.getIsinvoicestop()
            );

            if (customer.getQuotas() != null && !customer.getQuotas().isEmpty()) {
                for (CustomerQuota quota : customer.getQuotas()) {
                    saveQuota(quota);

                    //TODO Arpit : Commented below code because already inserted data in tblcustpackagerel from data getting from kafka message
//                PostpaidPlan planDetails = getPlanDetailsSafely(quota.getPlanId());
//                try {
//                    String sql = "select max(custpackageid) from tblcustpackagerel";
//                    BigInteger nextId = jdbcTemplate.queryForObject(sql, BigInteger.class);
//                    if(nextId == null) {
//                        nextId = BigInteger.ONE;
//                    }
//                    nextId = nextId.add(BigInteger.ONE);
//                    jdbcTemplate.update(DiameterSQLConstants.INSERT_CUSTOMER_PACKAGE_REL,
//                            nextId,
//                            customer.getCustId(),
//                            quota.getPlanId(),
//                            planDetails != null ? planDetails.getStartDate(): null,
//                            planDetails != null ? planDetails.getEndDate(): null,
//                            customer.getExpiryDate() != null ? Timestamp.valueOf(customer.getExpiryDate()): null,
//                            planDetails != null ? planDetails.getStatus(): null,
//                            customer.getCreateDate()
//                    );
//                    logger.debug("Inserted customer-package relation for customer={}, planId={}", customer.getCustId(), quota.getPlanId());
//                } catch (DataAccessException dae) {
//                    logger.error("Failed to insert customer package relation for customer={}, planId={}: {}",
//                            customer.getCustId(), quota.getPlanId(), dae.getMessage(), dae);
//                    throw new ValidationException("Customer already exisits with given Username and MVNOID");
//                }
                }
            }

            logger.debug("Customer created successfully.");
            return customer;
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
    public Customer updateCustomer(Customer customer) throws ValidationException {
        try {
            logger.debug("Update customer with ID: {}", customer.getCustId());
            Integer userCount = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_USERNAME_EXISTS_FOR_UPDATE, Integer.class, customer.getUserName(), customer.getMvnoId(), customer.getCustId());

            if (userCount != null && userCount > 0) {
                throw new ValidationException("Customer already exisits with given Username and MVNOID");
            }

            jdbcTemplate.update(DiameterSQLConstants.UPDATE_CUSTOMER, customer.getUserName(), customer.getPassword(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getCStatus(), customer.getLastLoginTime(), customer.getFailCount(), customer.getLastPasswordChange(), customer.getAccountNumber(), customer.getAccountType(), customer.getBirthDate(), customer.getCountry(), customer.getCui(), customer.getCustomerType(), customer.getGender(), customer.getImsi(), customer.getPhone(), customer.getSubscriberPackage(), customer.getSubscriberPackageId(), customer.getCreateDate(), customer.getExpiryDate(), customer.getLastStatusChangeDate(), customer.getNextBillDate(), customer.getLastBillDate(), customer.getBillDay(), customer.getOutstandingBalance(), customer.getPartnerId(), customer.getAsnNumber(), customer.getBngRouterInterface(), customer.getBngRouterName(), customer.getIpPrefixes(), customer.getIpv6Prefixes(), customer.getLanIp(), customer.getLanIpv6(), customer.getLlAccountId(), customer.getLlConnectionType(), customer.getLlExpiryDate(), customer.getLlMedium(), customer.getLlServiceId(), customer.getMacAddress(), customer.getPeerIp(), customer.getPoolIp(), customer.getQos(), customer.getRdExport(), customer.getRdValue(), customer.getVlanId(), customer.getVrfName(), customer.getVsiId(), customer.getVsiName(), customer.getWanIp(), customer.getWanIpv6(), customer.getBillEntityName(), customer.getPurchaseOrder(), customer.getRemarks(), customer.getAddParam1(), customer.getAddParam2(), customer.getAddParam3(), customer.getAddParam4(), customer.getParentCustId(), customer.getInvoiceOption(), customer.getOldPassword1(), customer.getOldPassword2(), customer.getOldPassword3(), customer.getFirstActivationDate(), customer.getAllowedIpAddrs(), customer.getSelfCarePwd(), customer.getTitle(), customer.getCustName(), customer.getContactPerson(), customer.getCafNo(), customer.getPan(), customer.getGst(), customer.getAadhar(), customer.getMacTelFlag(), customer.getMobile(), customer.getAltMobile(), customer.getAltPhone(), customer.getAltEmail(), customer.getFax(), customer.getResellerId(), customer.getSalesRepId(), customer.getDeposit(), customer.getVoiceSrvType(), customer.getDidNo(), customer.getChildDidNo(), customer.getIntercomNo(), customer.getIntercomGrp(), customer.getOnlineRenewalFlag(), customer.getVoipEnableFlag(), customer.getCustCategory(), customer.getWalletBalance(), customer.getNetworkType(), customer.getOltSlotId(), customer.getOltPortId(), customer.getStrConnType(), customer.getStrOltName(), customer.getStrSlotName(), customer.getStrPortName(), customer.getCreatedByStaffId(), customer.getLastModifiedByStaffId(), customer.getServiceAreaId(), customer.getNetworkDeviceId(), customer.getOnuId(), customer.getIsDeleted(), customer.getDefaultPoolId(), customer.getOtp(), customer.getOtpValidate(), customer.getCreateByName(), customer.getUpdateByName(), customer.getLastModifiedDate(), customer.getLatitude(), customer.getLongitude(), customer.getUrl(), customer.getGisCode(), customer.getVoiceProvision(), customer.getSalesRemark(), customer.getServiceType(), customer.getPreviousCafApprover(), customer.getNextCafApprover(), customer.getCafApproveStatus(), customer.getMvnoId(), customer.getCalendarType(), customer.getInvoiceType(), customer.getNasPort(), customer.getFramedIp(), customer.getFramedIpBind(), customer.getIpPoolNameBind(), customer.getBuid(), customer.getMaxConcurrentSession(), customer.getGatewayIp(), customer.getSkipNetConf(), customer.getRdImport(), customer.getMvnoDeactivationFlag(), customer.getIpv4(), customer.getIpv6(), customer.getVlan(), customer.getNasPortId(), customer.getNasIpAddress(), customer.getFramedIpv6Address(), customer.getDelegatedPrefix(), customer.getFramedRoute(), customer.getMacProvision(), customer.getMacAuthEnable(), customer.getFramedIpNetmask(), customer.getFramedIpv6Prefix(), customer.getPrimaryDns(), customer.getPrimaryIpv6Dns(), customer.getSecondaryIpv6Dns(), customer.getSecondaryDns(), customer.getMacRetentionPeriod(), customer.getMacRetentionUnit(), customer.getNextQuotaResetDate(), customer.getBlockNo(),customer.getIsinvoicestop(),customer.getCustId()
            );

            if (customer.getQuotas() != null && !customer.getQuotas().isEmpty()) {
                for (CustomerQuota quota : customer.getQuotas()) {
                    saveQuota(quota);

                    //TODO Arpit : Commented below code because already inserted data in tblcustpackagerel from data getting from kafka message
//                PostpaidPlan planDetails = getPlanDetailsSafely(quota.getPlanId());
//                try {
//                    String sql = "select max(custpackageid) from tblcustpackagerel";
//                    BigInteger nextId = jdbcTemplate.queryForObject(sql, BigInteger.class);
//                    if(nextId == null) {
//                        nextId = BigInteger.ONE;
//                    }
//                    nextId = nextId.add(BigInteger.ONE);
//                    jdbcTemplate.update(DiameterSQLConstants.INSERT_CUSTOMER_PACKAGE_REL,
//                            nextId,
//                            customer.getCustId(),
//                            quota.getPlanId(),
//                            planDetails != null ? planDetails.getStartDate(): null,
//                            planDetails != null ? planDetails.getEndDate(): null,
//                            customer.getExpiryDate() != null ? Timestamp.valueOf(customer.getExpiryDate()): null,
//                            planDetails != null ? planDetails.getStatus(): null,
//                            customer.getCreateDate()
//                    );
//                    logger.debug("Inserted customer-package relation for customer={}, planId={}", customer.getCustId(), quota.getPlanId());
//                } catch (DataAccessException dae) {
//                    logger.error("Failed to insert customer package relation for customer={}, planId={}: {}",
//                            customer.getCustId(), quota.getPlanId(), dae.getMessage(), dae);
//                    throw new ValidationException("Customer already exisits with given Username and MVNOID");
//                }
                }
            }

            logger.debug("Customer created successfully.");
            return customer;
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @SuppressWarnings("serial")
	public static class RepositoryException extends RuntimeException {
        public RepositoryException(String message, Throwable cause) { super(message, cause); }
    }
    
    @SuppressWarnings("deprecation")
	private PostpaidPlan getPlanDetailsSafely(BigInteger planId) {
    	 try {
    	        return jdbcTemplate.queryForObject(
    	                DiameterSQLConstants.GET_PLAN_DETAILS,
    	                new Object[]{planId},
    	                (rs, rowNum) -> {
    	                    PostpaidPlan plan = new PostpaidPlan();
                            plan.setStartDate(
                                    rs.getTimestamp("STARTDATE")
                                            .toLocalDateTime()
                                            .toLocalDate()
                            );
                            plan.setEndDate(
                                    rs.getTimestamp("ENDDATE")
                                            .toLocalDateTime()
                                            .toLocalDate()
                            );

    	                    plan.setStatus(rs.getString("STATUS"));
    	                    return plan;
    	                }
    	        );
    	    } catch (EmptyResultDataAccessException ex) {
    	        return null;
    	    }
    }
    
    public void saveQuota(CustomerQuota quota) throws ValidationException {
    	
            Integer count = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_QUOTA_EXISTS, Integer.class,
            		quota.getCustId(), quota.getPlanId(), quota.getCustPackageId());

            if (count != null && count > 0) {
                throw new ValidationException("Quota already exists for given custId, planId, and packageId");
            }
    	
        jdbcTemplate.update(DiameterSQLConstants.INSERT_QUOTA,quota.getQuotaDtlsId(),quota.getCustId(),quota.getPlanId(),quota.getQuotaType(),quota.getTotalQuota(),quota.getUsedQuota(),quota.getQuotaUnit(),quota.getTimeTotalQuota(),quota.getTimeQuotaUsed(),quota.getTimeQuotaUnit(),quota.getCreatedByStaffId(),quota.getCreateDate(),quota.getLastModifiedByStaffId(),quota.getLastModifiedDate(),quota.getIsDeleted(),quota.getTotalQuotaKb(),quota.getUsedQuotaKb(),quota.getTimeUsedQuotaSec(),quota.getTimeTotalQuotaSec(),quota.getCustPackageId(),quota.getDidTotalQuota(),quota.getDidUsedQuota(),quota.getIntercomTotalQuota(),quota.getIntercomUsedQuota(),quota.getDidQuotaUnit(),quota.getIntercomQuotaUnit(),quota.getCreateByName(),quota.getUpdateByName(),quota.getSpeedDowngradeFlag(),quota.getIsFupApplied(),quota.getFupAppliedDate(),quota.getCurrentSessionUsageTime(),quota.getCurrentSessionUsageVolume(),quota.getParentQuotaType(),quota.getIsChunkAvailable(),quota.getReservedQuotaInPer(),quota.getTotalReservedQuota(),quota.getUsageQuotaType(),quota.getSkipQuotaUpdate(),quota.getLastQuotaReset(),quota.getIsQuotaUpdateSkipped());
    }

    public boolean quotaExists(BigInteger custId, BigInteger planId, Integer custPackageId) {
        Integer count = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_QUOTA_EXISTS, Integer.class,
                custId, planId, custPackageId);
        return count != null && count > 0;
    }
    public List<Customer> getAllCustomers() {
        List<Customer> customers = jdbcTemplate.query(DiameterSQLConstants.GET_ALL_CUSTOMERS, customerRowMapper());

        if (!customers.isEmpty()) {
            Map<BigInteger, List<CustomerQuota>> quotaMap = fetchQuotasByCustomerIds(
                    customers.stream().map(Customer::getCustId).collect(Collectors.toList())
            );

            for (Customer customer : customers) {
                customer.setQuotas(quotaMap.getOrDefault(customer.getCustId(), new ArrayList<>()));
            }
        }

        return customers;
    }

    public Map<String,Object>
    getAllCustomersPaginated(int page, int size){

        int offset = page * size;

        String sql = DiameterSQLConstants.GET_ALL_CUSTOMERS + " LIMIT ? OFFSET ?";

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper(), size, offset);

        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tblcustomers", Integer.class);

        if(!customers.isEmpty()){

            Map<BigInteger, List<CustomerQuota>> quotaMap = fetchQuotasByCustomerIds(customers.stream().map(Customer::getCustId).collect(Collectors.toList()));

            customers.forEach(customer ->
                    customer.setQuotas(
                            quotaMap.getOrDefault(
                                    customer.getCustId(),
                                    new ArrayList<>()
                            )
                    )
            );
        }


        Map<String,Object> response = new HashMap<>();


        response.put("content", customers);

        response.put("page",page);

        response.put("size", size);

        response.put("totalElements", total);

        response.put("totalPages", (int)Math.ceil((double) total / size));

        return response;
    }

    @SuppressWarnings("deprecation")
	private Map<BigInteger, List<CustomerQuota>> fetchQuotasByCustomerIds(List<BigInteger> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) return new HashMap<>();

        String inSql = String.join(",", Collections.nCopies(customerIds.size(), "?"));
        String finalSql = String.format(DiameterSQLConstants.GET_QUOTA_BY_CUSTOMER_IDs, inSql);
        List<CustomerQuota> quotas = jdbcTemplate.query(finalSql, customerIds.toArray(), quotaRowMapper());
        return quotas.stream().collect(Collectors.groupingBy(CustomerQuota::getCustId));
    }
    
    public List<Customer> getAllCustomersWithQuotas() {
        List<Customer> customers = jdbcTemplate.query(DiameterSQLConstants.GET_ALL_CUSTOMERS, customerRowMapper());

        if (!customers.isEmpty()) {
            Map<BigInteger, List<CustomerQuota>> quotaMap = fetchQuotasByCustomerIds(
                    customers.stream().map(Customer::getCustId).collect(Collectors.toList())
            );

            for (Customer customer : customers) {
                customer.setQuotas(quotaMap.getOrDefault(customer.getCustId(), new ArrayList<>()));
            }
        }

        return customers;
    }

    public Customer getCustomerByIdWithQuotas(String custId) {
        Customer customer = jdbcTemplate.queryForObject(DiameterSQLConstants.GET_CUSTOMER_BY_ID, customerRowMapper(), BigDecimal.valueOf(Long.valueOf(custId)));
        if (customer != null) {
        	setActiveQuotas(customer,null);
        }
        return customer;
    }

    public Customer getCustomerByNameWithQuotas(String custName) {
        Customer customer = jdbcTemplate.queryForObject(DiameterSQLConstants.GET_CUSTOMER_BY_NAME, customerRowMapper(), custName);
        if (customer != null) {
            setActiveQuotas(customer,null);
        }
        return customer;
    }
    
    public Customer getCustomerByUserNameWithQuotas(String userName) {
    	Customer customer = null;
    	try {
    		 customer = getFirstCustomer(DiameterSQLConstants.GET_CUSTOMER_BY_USERNAME, userName);
    	}catch(Exception e) {
    		logger.error("Failed to fetch customer by username {}", userName, e);
    	}
    	if (customer == null) {
        	customer = getFirstCustomer(DiameterSQLConstants.GET_CUSTOMER_BY_PHONE, userName);
        }
        if (customer != null) {
            setActiveQuotas(customer,userName);
        }
        return customer;
    }

    private void setActiveQuotas(Customer customer,String strCustomerId) {
    	List<CustomerPackageRel> customerPackageRels = null;
    	boolean bValid=false;
    	if(strCustomerId !=null && !strCustomerId.isEmpty()) {
    		customerPackageRels = getActiveCustomerPackageRelByCustIdAndSubscriber(customer.getCustId(),strCustomerId);
    		if (!customerPackageRels.isEmpty()) {
    			CustomerPackageRel customerPackageRel = customerPackageRels.get(0);
                List<CustomerQuota> quotas = getQuotasByCustomerId(
                        customer.getCustId(),
                        customerPackageRel.getPlanId(),
                        customerPackageRel.getCustPackageId());
                customer.setQuotas(quotas);
                if(!quotas.isEmpty()) {
                	bValid= true;
                }
                logger.info("Selected active package for customer={}, custPackageId={}, planId={}, custServiceMappingId={}",
                        customer.getCustId(),
                        customerPackageRel.getCustPackageId(),
                        customerPackageRel.getPlanId(),
                        customerPackageRel.getCustServiceMappingId());
    		}
    		
    	}
    	if(!bValid) {
    		logger.warn("No active customer package found for customer={}. Falling back to latest quota.",
                    customer.getCustId());
    		customerPackageRels = getActiveCustomerPackageRel(customer.getCustId());
            if (!customerPackageRels.isEmpty()) {
                CustomerPackageRel customerPackageRel = customerPackageRels.get(0);
                List<CustomerQuota> quotas = getQuotasByCustomerId(
                        customer.getCustId(),
                        customerPackageRel.getPlanId(),
                        customerPackageRel.getCustPackageId());
                customer.setQuotas(quotas);
                logger.info("Selected active package for customer={}, custPackageId={}, planId={}, custServiceMappingId={}",
                        customer.getCustId(),
                        customerPackageRel.getCustPackageId(),
                        customerPackageRel.getPlanId(),
                        customerPackageRel.getCustServiceMappingId());
            }
    	}
        
    }
    
    private Customer getFirstCustomer(String sql, String identifier) {
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper(), identifier);

        return customers.stream()
                .findFirst()
                .orElse(null);
    }
    
    private List<CustomerQuota> getQuotasByCustomerId(BigInteger custId,BigInteger planId) {
        return jdbcTemplate.query(DiameterSQLConstants.GET_QUOTA_BY_CUSTOMER_ID_AND_PLAN_ID, quotaRowMapper(), custId,planId);
    }

    private List<CustomerQuota> getQuotasByCustomerId(BigInteger custId, BigInteger planId, BigInteger custPackageId) {
        return jdbcTemplate.query(
                DiameterSQLConstants.GET_QUOTA_BY_CUSTOMER_ID_PLAN_ID_AND_PACKAGE_ID,
                quotaRowMapper(),
                custId,
                planId,
                custPackageId);
    }

    private List<CustomerQuota> getQuotasByCustomerId(BigInteger custId) {
        return jdbcTemplate.query(DiameterSQLConstants.GET_QUOTA_BY_CUSTOMER_ID, quotaRowMapper(), custId);
    }

    public Optional<Customer> findByCustId(BigInteger custId) {

        List<Customer> customers = jdbcTemplate.query(
                DiameterSQLConstants.GET_CUSTOMER_BY_ID,
                customerRowMapper(),
                custId
        );

        return customers.stream().findFirst();
    }


    
    public void updateQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId,long custPackageId) {
        jdbcTemplate.update(DiameterSQLConstants.UPDATE_QUOTA_BY_CUSTOMER_ID, usedQuota, custId, planId,custPackageId);
    }
    
    public void updateTimeQuotaByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId) {
        jdbcTemplate.update(DiameterSQLConstants.UPDATE_TIME_QUOTA_BY_CUSTOMER_ID, usedQuota, custId, planId);
    }
    
    public void updateSmsQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId) {
        jdbcTemplate.update(DiameterSQLConstants.UPDATE_SMS_QUOTA_BY_CUSTOMER_ID, usedQuota, custId, planId,custPackageId);
    }
    
    public void updateVoiceQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId) {
        jdbcTemplate.update(DiameterSQLConstants.UPDATE_VOICE_QUOTA_BY_CUSTOMER_ID, usedQuota, custId, planId,custPackageId);
    }
    
    public void updateFupStatusByQuotaId(BigInteger quotaId) {
    	jdbcTemplate.update(DiameterSQLConstants.UPDATE_FUP_STATUS_BY_QUOTA_ID, quotaId);
    }
    
    public List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId) {
        return getActiveCustomerPackageRel(custId);
    }

    private List<CustomerPackageRel> getActiveCustomerPackageRel(BigInteger custId) {
        return jdbcTemplate.query(
                DiameterSQLConstants.GET_ACTIVE_PACKAGE_BY_CUSTOMER_ID,
                customerPackageRelRowMapper(),
                custId);
    }
    
    private List<CustomerPackageRel> getActiveCustomerPackageRelByCustIdAndSubscriber(BigInteger custId,String strSubscriber) {
        return jdbcTemplate.query(
                DiameterSQLConstants.GET_ACTIVE_PACKAGE_BY_CUSTOMER_ID_AND_SUBSCRIBER,
                customerPackageRelRowMapper(),
                custId,strSubscriber,strSubscriber);
    }

    public List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId, BigInteger planId, BigInteger custPackageId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM tblcustpackagerel WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (custId != null) {
            sql.append(" AND custid=?");
            params.add(custId);
        }

        if (planId != null) {
            sql.append(" AND planid=?");
            params.add(planId);
        }

        if (custPackageId != null) {
            sql.append(" AND custpackageid=?");
            params.add(custPackageId);
        }

        return jdbcTemplate.query(sql.toString(),
                params.toArray(),
                customerPackageRelRowMapper());
    }

    public void updateCustomerPackageRel(Timestamp endDate, Timestamp expiryDate, BigInteger custPackageId, BigInteger custId) {
        String sql = "UPDATE tblcustpackagerel SET enddate = ?, expirydate = ? WHERE custpackageid = ? AND custid = ?";

        int updated= jdbcTemplate.update(sql, endDate, expiryDate, custPackageId, custId);

        logger.debug("Executed Static DB update for tblcustpackagerel with custPackageId={} and custId={} with Updated rows count = {}", custPackageId, custId, updated);
    }

    private RowMapper<Customer> customerRowMapper() {
        return (rs, rowNum) -> {
            Customer c = new Customer();
            c.setCustId(rs.getObject("custid", java.math.BigInteger.class));
            c.setUserName(rs.getString("username"));
            c.setPassword(rs.getString("password"));
            c.setFirstName(rs.getString("firstname"));
            c.setLastName(rs.getString("lastname"));
            c.setEmail(rs.getString("email"));
            c.setCStatus(rs.getString("cstatus"));
            c.setLastLoginTime(rs.getTimestamp("last_login_time"));
            c.setFailCount(rs.getInt("failcount"));
            Timestamp ts = rs.getTimestamp("last_password_change");
            c.setLastPasswordChange(ts != null ? ts.toLocalDateTime() : null);
            c.setAccountNumber(rs.getString("accountnumber"));
            c.setAccountType(rs.getString("accounttype"));
            c.setBirthDate(rs.getTimestamp("birthdate"));
            c.setCountry(rs.getString("country"));
            c.setCui(rs.getString("cui"));
            c.setCustomerType(rs.getString("customertype"));
            c.setGender(rs.getString("gender"));
            c.setImsi(rs.getString("imsi"));
            c.setPhone(rs.getString("phone"));
            c.setSubscriberPackage(rs.getString("subscriberpackage"));
            c.setSubscriberPackageId(rs.getString("subscriberpackageid"));
            c.setCreateDate(rs.getTimestamp("createdate"));
            c.setExpiryDate(rs.getString("expirydate"));
            c.setLastStatusChangeDate(
                    rs.getTimestamp("laststatuschangedate") != null ?
                            rs.getTimestamp("laststatuschangedate").toLocalDateTime() : null
            );

            c.setNextBillDate(
                    rs.getDate("NEXTBILLDATE") != null ?
                            rs.getDate("NEXTBILLDATE").toLocalDate() : null
            );

            c.setLastBillDate(
                    rs.getDate("LASTBILLDATE") != null ?
                            rs.getDate("LASTBILLDATE").toLocalDate() : null
            );


            c.setBillDay(rs.getBigDecimal("BILLDAY"));
            c.setOutstandingBalance(rs.getBigDecimal("outstandingbalance"));
            c.setPartnerId(rs.getObject("partnerid", java.math.BigInteger.class));
            c.setAsnNumber(rs.getString("ASNNumber"));
            c.setBngRouterInterface(rs.getString("BNGRouterInterface"));
            c.setBngRouterName(rs.getString("BNGRouterName"));
            c.setIpPrefixes(rs.getString("IPPrefixes"));
            c.setIpv6Prefixes(rs.getString("IPV6Prefixes"));
            c.setLanIp(rs.getString("LANIP"));
            c.setLanIpv6(rs.getString("LANIPV6"));
            c.setLlAccountId(rs.getString("LLAccountID"));
            c.setLlConnectionType(rs.getString("LLConnectionType"));
            c.setLlExpiryDate(rs.getString("LLExpiryDate"));
            c.setLlMedium(rs.getString("LLMedium"));
            c.setLlServiceId(rs.getString("LLServiceID"));
            c.setMacAddress(rs.getString("MACADDRESS"));
            c.setPeerIp(rs.getString("PeerIP"));
            c.setPoolIp(rs.getString("POOLIP"));
            c.setQos(rs.getString("QOS"));
            c.setRdExport(rs.getString("RDExport"));
            c.setRdValue(rs.getString("RDValue"));
            c.setVlanId(rs.getString("VLANID"));
            c.setVrfName(rs.getString("VRFName"));
            c.setVsiId(rs.getString("VSIID"));
            c.setVsiName(rs.getString("VSIName"));
            c.setWanIp(rs.getString("WANIP"));
            c.setWanIpv6(rs.getString("WANIPV6"));
            c.setBillEntityName(rs.getString("billentityname"));
            c.setPurchaseOrder(rs.getString("purchaseorder"));
            c.setRemarks(rs.getString("remarks"));
            c.setAddParam1(rs.getString("addparam1"));
            c.setAddParam2(rs.getString("addparam2"));
            c.setAddParam3(rs.getString("addparam3"));
            c.setAddParam4(rs.getString("addparam4"));
            c.setParentCustId(rs.getInt("parentcustid"));
            c.setInvoiceOption(rs.getString("invoiceoption"));
            c.setOldPassword1(rs.getString("oldpassword1"));
            c.setOldPassword2(rs.getString("oldpassword2"));
            c.setOldPassword3(rs.getString("oldpassword3"));
            c.setFirstActivationDate(
                    rs.getTimestamp("firstactivationdate") != null ?
                            rs.getTimestamp("firstactivationdate").toLocalDateTime() : null
            );
            c.setAllowedIpAddrs(rs.getString("allowedipaddrs"));
            c.setSelfCarePwd(rs.getString("selfcarepwd"));
            c.setTitle(rs.getString("title"));
            c.setCustName(rs.getString("custname"));
            c.setContactPerson(rs.getString("contactperson"));
            c.setCafNo(rs.getString("cafno"));
            c.setPan(rs.getString("pan"));
            c.setGst(rs.getString("gst"));
            c.setAadhar(rs.getString("aadhar"));
            c.setMacTelFlag(rs.getShort("mactelflag"));
            c.setMobile(rs.getString("mobile"));
            c.setAltMobile(rs.getString("altmobile"));
            c.setAltPhone(rs.getString("altphone"));
            c.setAltEmail(rs.getString("altemail"));
            c.setFax(rs.getString("fax"));
            c.setResellerId(rs.getObject("resellerid", java.math.BigInteger.class));
            c.setSalesRepId(rs.getObject("salesrepid", java.math.BigInteger.class));
            c.setDeposit(rs.getBigDecimal("deposit"));
            c.setVoiceSrvType(rs.getString("voicesrvtype"));
            c.setDidNo(rs.getString("didno"));
            c.setChildDidNo(rs.getString("childdidno"));
            c.setIntercomNo(rs.getString("intercomno"));
            c.setIntercomGrp(rs.getString("intercomgrp"));
            c.setOnlineRenewalFlag(rs.getShort("onlinerenewalflag"));
            c.setVoipEnableFlag(rs.getShort("voipenableflag"));
            c.setCustCategory(rs.getString("custcategory"));
            c.setWalletBalance(rs.getBigDecimal("walletbalance"));
            c.setNetworkType(rs.getString("networktype"));
            c.setOltSlotId(rs.getObject("oltslotid", java.math.BigInteger.class));
            c.setOltPortId(rs.getObject("oltportid", java.math.BigInteger.class));
            c.setStrConnType(rs.getString("strconntype"));
            c.setStrOltName(rs.getString("stroltname"));
            c.setStrSlotName(rs.getString("strslotname"));
            c.setStrPortName(rs.getString("strportname"));
            c.setCreatedByStaffId(rs.getInt("createdbystaffid"));
            c.setLastModifiedByStaffId(rs.getInt("lastmodifiedbystaffid"));
            c.setServiceAreaId(rs.getObject("servicearea_id", java.math.BigInteger.class));
            c.setNetworkDeviceId(rs.getObject("network_device_id", java.math.BigInteger.class));
            c.setOnuId(rs.getString("onuid"));
            c.setIsDeleted(rs.getShort("is_deleted"));
            c.setDefaultPoolId(rs.getObject("defaultpoolid", java.math.BigInteger.class));
            c.setOtp(rs.getString("otp"));
            c.setOtpValidate(
                    rs.getTimestamp("otpvalidate") != null ?
                            rs.getTimestamp("otpvalidate").toLocalDateTime() : null
            );
            c.setCreateByName(rs.getString("createbyname"));
            c.setUpdateByName(rs.getString("updatebyname"));
            c.setLastModifiedDate(rs.getTimestamp("lastmodifieddate"));
            c.setLatitude(rs.getString("latitude"));
            c.setLongitude(rs.getString("longitude"));
            c.setUrl(rs.getString("url"));
            c.setGisCode(rs.getString("gis_code"));
            c.setVoiceProvision(rs.getShort("voiceProvision"));
            c.setSalesRemark(rs.getString("salesremark"));
            c.setServiceType(rs.getString("servicetype"));
            c.setPreviousCafApprover(rs.getObject("previous_caf_approver", java.math.BigInteger.class));
            c.setNextCafApprover(rs.getObject("next_caf_approver", java.math.BigInteger.class));
            c.setCafApproveStatus(rs.getString("caf_approve_status"));
            c.setMvnoId(rs.getObject("MVNOID", java.math.BigInteger.class));
            c.setCalendarType(rs.getString("calendartype"));
            c.setInvoiceType(rs.getString("invoice_type"));
            c.setNasPort(rs.getString("nas_port"));
            c.setFramedIp(rs.getString("framed_ip"));
            c.setFramedIpBind(rs.getString("framed_ip_bind"));
            c.setIpPoolNameBind(rs.getString("ip_pool_name_bind"));
            c.setBuid(rs.getObject("BUID", java.math.BigInteger.class));
            c.setMaxConcurrentSession(rs.getObject("maxconcurrentsession", java.math.BigInteger.class));
            c.setGatewayIp(rs.getString("gatewayip"));
            c.setSkipNetConf(rs.getString("skipnetconf"));
            c.setRdImport(rs.getString("rdimport"));
            c.setMvnoDeactivationFlag(rs.getShort("mvno_deactivation_flag"));
            c.setIpv4(rs.getString("ipv4"));
            c.setIpv6(rs.getString("ipv6"));
            c.setVlan(rs.getString("vlan"));
            c.setNasPortId(rs.getString("nas_port_id"));
            c.setNasIpAddress(rs.getString("nas_ip_address"));
            c.setFramedIpv6Address(rs.getString("framed_ipv6_address"));
            c.setDelegatedPrefix(rs.getString("delegatedprefix"));
            c.setFramedRoute(rs.getString("framedroute"));
            c.setMacProvision(rs.getShort("mac_provision"));
            c.setMacAuthEnable(rs.getShort("mac_auth_enable"));
            c.setFramedIpNetmask(rs.getString("framed_ip_netmask"));
            c.setFramedIpv6Prefix(rs.getString("framed_ipv6_prefix"));
            c.setPrimaryDns(rs.getString("primary_dns"));
            c.setPrimaryIpv6Dns(rs.getString("primary_ipv6_dns"));
            c.setSecondaryIpv6Dns(rs.getString("secondary_ipv6_dns"));
            c.setSecondaryDns(rs.getString("secondary_ipv6_dns"));
            c.setMacRetentionPeriod(rs.getObject("macretentionperiod", java.math.BigInteger.class));
            c.setMacRetentionUnit(rs.getString("macretentionunit"));
            c.setNextQuotaResetDate(
                    rs.getDate("nextquotaresetdate") != null ?
                            rs.getDate("nextquotaresetdate").toLocalDate() : null
            );
            c.setBlockNo(rs.getString("blockno"));
            return c;
        };
    }


    private RowMapper<CustomerQuota> quotaRowMapper() {
        return (rs, rowNum) -> {
            CustomerQuota q = new CustomerQuota();
            q.setQuotaDtlsId(rs.getObject("quotadtlsid", java.math.BigInteger.class));
            q.setCustId(rs.getObject("custid", java.math.BigInteger.class));
            q.setPlanId(rs.getObject("planid", java.math.BigInteger.class));
            q.setQuotaType(rs.getString("quotatype"));
            q.setTotalQuota(rs.getBigDecimal("totalquota"));
            q.setUsedQuota(rs.getBigDecimal("usedquota"));
            q.setQuotaUnit(rs.getString("quotaunit"));
            q.setTimeTotalQuota(rs.getBigDecimal("timetotalquota"));
            q.setTimeQuotaUsed(rs.getBigDecimal("timequotaused"));
            q.setTimeQuotaUnit(rs.getString("timequotaunit"));
            q.setCreatedByStaffId(rs.getBigDecimal("CREATEDBYSTAFFID"));
            q.setCreateDate(rs.getTimestamp("createdate"));
            q.setLastModifiedByStaffId(rs.getBigDecimal("lastmodifiedbystaffid"));
            q.setLastModifiedDate(rs.getTimestamp("lastmodifieddate"));
            q.setIsDeleted(rs.getBoolean("is_deleted"));
            q.setTotalQuotaKb(rs.getBigDecimal("totalquotakb"));
            q.setUsedQuotaKb(rs.getBigDecimal("usedquotakb"));
            q.setTimeUsedQuotaSec(rs.getBigDecimal("timeusedquotasec"));
            q.setTimeTotalQuotaSec(rs.getBigDecimal("timetotalquotasec"));
            q.setCustPackageId(rs.getInt("custpackageid"));
            q.setDidTotalQuota(rs.getBigDecimal("didtotalquota"));
            q.setDidUsedQuota(rs.getBigDecimal("didusedquota"));
            q.setIntercomTotalQuota(rs.getBigDecimal("intercomtotalquota"));
            q.setIntercomUsedQuota(rs.getBigDecimal("intercomusedquota"));
            q.setDidQuotaUnit(rs.getString("did_quota_unit"));
            q.setIntercomQuotaUnit(rs.getString("intercom_quota_unit"));
            q.setCreateByName(rs.getString("createbyname"));
            q.setUpdateByName(rs.getString("updatebyname"));
            q.setSpeedDowngradeFlag(rs.getBoolean("speeddowngradeflag"));
            q.setIsFupApplied(rs.getBoolean("isfupapllied"));
            q.setFupAppliedDate(rs.getTimestamp("fupapplieddate"));
            q.setCurrentSessionUsageTime(rs.getBigDecimal("currentsessionusagetime"));
            q.setCurrentSessionUsageVolume(rs.getBigDecimal("currentsessionusagevolume"));
            q.setParentQuotaType(rs.getString("parnet_quota_type"));
            q.setIsChunkAvailable(rs.getBoolean("is_chunk_available"));
            q.setReservedQuotaInPer(rs.getDouble("reserved_quota_in_per"));
            q.setTotalReservedQuota(rs.getDouble("total_reserved_quota"));
            q.setUsageQuotaType(rs.getString("usage_quota_type"));
            q.setSkipQuotaUpdate(rs.getBoolean("skip_quota_update"));
            q.setLastQuotaReset(rs.getTimestamp("last_quota_reset"));
            q.setIsQuotaUpdateSkipped(rs.getBoolean("isquotaupdateskipped"));
            return q;
        };
    }


    private RowMapper<CustomerPackageRel> customerPackageRelRowMapper() {

        return (rs, rowNum) -> {

            CustomerPackageRel r = new CustomerPackageRel();

            r.setCustPackageId(rs.getObject("custpackageid", BigInteger.class));
            r.setCustId(rs.getObject("custid", BigInteger.class));
            r.setPlanId(rs.getObject("planid", BigInteger.class));

            r.setStartDate(rs.getTimestamp("startdate"));
            r.setEndDate(rs.getTimestamp("enddate"));
            r.setExpiryDate(rs.getTimestamp("expirydate"));

            r.setStatus(rs.getString("status"));
            r.setService(rs.getString("service"));

            r.setQosPolicyId(rs.getObject("qospolicyid", BigInteger.class));
            r.setUploadQos(rs.getString("uploadqos"));
            r.setDownloadQos(rs.getString("downloadqos"));
            r.setUploadTs(rs.getString("uploadts"));
            r.setDownloadTs(rs.getString("downloadts"));

            r.setCreatedByStaffId(rs.getBigDecimal("createdbystaffid"));
            r.setCreateDate(rs.getTimestamp("createdate"));
            r.setLastModifiedByStaffId(rs.getBigDecimal("lastmodifiedbystaffid"));
            r.setLastModifiedDate(rs.getTimestamp("lastmodifieddate"));

            r.setDelete(rs.getBoolean("is_delete"));

            r.setOfferPrice(rs.getDouble("offer_price"));
            r.setTaxAmount(rs.getDouble("tax_amount"));

            r.setCreateByName(rs.getString("createbyname"));
            r.setUpdateByName(rs.getString("updatebyname"));

            r.setCreditDocId(rs.getObject("creditdocid", BigInteger.class));
            r.setDebitDocId(rs.getObject("debitdocid", BigInteger.class));

            r.setWalletBalUsed(rs.getDouble("wallet_bal_used"));

            r.setPurchaseType(rs.getString("purchase_type"));
            r.setOnlinePurchaseId(rs.getObject("online_purchase_id", BigInteger.class));
            r.setPurchaseFrom(rs.getString("purchase_from"));

            r.setGraceDays(rs.getObject("grace_days", BigInteger.class));
            r.setCustPlanStatus(rs.getString("cust_plan_status"));
            r.setCustServiceMappingId(rs.getObject("custservicemappingid", BigInteger.class));

            r.setNotificationLevel(rs.getObject("notificationlevel", BigInteger.class));
            r.setTriggerCoadm(rs.getBoolean("istriggercoadm"));
            r.setOnQuotaExhaustEventName(rs.getString("onquotaexhausteventname"));

            return r;
        };
    }

    public CustomerQuota getLatestQuota(BigInteger custId) {

        String sql =
                "SELECT * " +
                        "FROM tblcustquotadtls " +
                        "WHERE custid = ? " +
                        "AND IFNULL(is_deleted, 0) = 0 " +
                        "ORDER BY quotadtlsid DESC " +
                        "LIMIT 1";

        List<CustomerQuota> quotas = jdbcTemplate.query(
                sql,
                quotaRowMapper(),
                custId
        );

        return quotas.isEmpty() ? null : quotas.get(0);
    }

    public int resetDailyQuotas() {
        return jdbcTemplate.update(DiameterSQLConstants.RESET_DAILY_QUOTAS);
    }

}

