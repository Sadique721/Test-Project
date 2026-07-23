package com.diameter.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import com.diameter.kafka.*;
import com.diameter.model.CustSmsDetails;
import com.diameter.model.CustVoiceDetails;
import com.diameter.model.Customer;
import com.diameter.model.CustomerPackageRel;


public interface CustomerService {

	Customer createCustomer(Customer customer) throws ValidationException;

	List<Customer> getCustomers(String id, String name, String userName);

    Map<String,Object> getCustomers(String id, String name, String userName, int page, int size);

	void updateQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, long custPackageId);
	
	List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId, BigInteger planId, BigInteger custPackageId);

	void saveCustomers(SaveCustomerDataShareMessage dataMessage) throws Exception;

	void updateCustomers(UpdateCustomerShareDataMessage message) throws ValidationException;
	
	void updateFupStatusByQuotaId(BigInteger quotaId);

    void updateCustomerStatus(CustomerUpdateMessage message);

    void updateServiceStatus(CustomerServiceActivationMessage message);
    
    List<CustSmsDetails> getCustomerSmsPackageRel(BigInteger custId, BigInteger planId, Long custPackageId);
    
    List<CustVoiceDetails> getCustomerVoicePackageRel(BigInteger custId, BigInteger planId, Long custPackageId);
    
    void updateSmsQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId);

    void updateCustomerPackageRel(CustomerPackageRelMessage message);
    
    void updateVoiceQuotasByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId, Long custPackageId);
    
    void updateTimeQuotaByCustomerId(BigDecimal usedQuota, BigInteger custId, BigInteger planId);

     void saveCustomersPlanAndServiceData(ChangePlanMessage message);

     void saveDiameterChangePlanSyncData(ChangePlanMessage message, Map<String, Object> syncData);

     void updateCustomerServiceMappingImsi(Integer customerId, String msisdn, String imsi);
     
     List<CustomerPackageRel> getCustomerPackageRel(BigInteger custId);

     void sendRAR(BigDecimal customerId);
    }
