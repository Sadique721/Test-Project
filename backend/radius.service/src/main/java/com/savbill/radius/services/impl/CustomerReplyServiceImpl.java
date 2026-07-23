package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.CustomerReply;
import com.savbill.radius.entity.QCustomerReply;
import com.savbill.radius.repository.CustomerReplyRepository;
import com.savbill.radius.repository.CustomerRepository;
import com.savbill.radius.services.CustomerReplyService;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class CustomerReplyServiceImpl implements CustomerReplyService 
{
	@Autowired
	private CustomerReplyRepository custReplyRepository;
	
	@Autowired
	private CustomerRepository custRepository;

	@Autowired
	private CustomerService customerService;
	
	private static final Logger log = LoggerFactory.getLogger(CustomerReplyServiceImpl.class);
	
	@Override
	public CustomerReply findCustomerReplyById(Long id, Integer mvnoId) {

		try {

			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id.");
			}
			QCustomerReply qCustomerReply = QCustomerReply.customerReply;
			BooleanExpression boolExp = qCustomerReply.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qCustomerReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qCustomerReply.attributeId.eq(id));

			Optional<CustomerReply> customerReply = custReplyRepository.findOne(boolExp);

			if(!customerReply.isPresent()) {
				throw new IllegalArgumentException("No record found with attribute id : '"+id+"' ,Please enter valid attribute id.");
			}

			return customerReply.get();
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private CustomerReply validateCustomerReplyForDeleteOrUpdate(Long id, Integer mvnoId) {

		try {

			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id.");
			}
			QCustomerReply qCustomerReply = QCustomerReply.customerReply;
			BooleanExpression boolExp = qCustomerReply.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qCustomerReply.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qCustomerReply.attributeId.eq(id));

			Optional<CustomerReply> customerReply = custReplyRepository.findOne(boolExp);

			if(!customerReply.isPresent()) {
				throw new IllegalArgumentException("You do not have access to update or delete this record.");
			}

			return customerReply.get();
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<CustomerReply> findCustomerReplyByCustomerId(Long id, Integer mvnoId) {
		
		try {
			
			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid customer id.");
			}
			QCustomerReply qCustomerReply = QCustomerReply.customerReply;
			BooleanExpression exp = qCustomerReply.isNotNull();
			exp = exp.and(qCustomerReply.customerId.eq(id));
			if(mvnoId == null || mvnoId != 1)
				exp = exp.and(qCustomerReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<CustomerReply>) custReplyRepository.findAll(exp);
			//return custReplyRepository.findCustomerReplyByCustomerId(id);
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<CustomerReply> findAllCustomerReply(Integer mvnoId)  {
		
		try {
			QCustomerReply qCustomerReply = QCustomerReply.customerReply;
			BooleanExpression exp = qCustomerReply.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return custReplyRepository.findAll();
			else {
				exp = exp.and(qCustomerReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<CustomerReply>) custReplyRepository.findAll(exp);
			}
		}
		catch (RuntimeException e)  {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public CustomerReply addCustomerReply(CustomerReply customerReply, Integer mvnoId) {
		
		try {
			
			customerReply.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validatCustomerReply(customerReply,false);
			customerReply.setCreatedOn(new Timestamp(new Date().getTime()));
			customerReply.setLastModifiedOn(new Timestamp(new Date().getTime()));
			return custReplyRepository.save(customerReply);
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void validatCustomerReply(CustomerReply custReplyVo,boolean isUpdate) {
		
		try {
			
			if(isUpdate) {
				
				if(custReplyVo.getAttributeId() == null || custReplyVo.getAttributeId() == 0) {
					throw new IllegalArgumentException("Please enter valid attribute id to update customer reply.");
				}
				CustomerReply customerReply = validateCustomerReplyForDeleteOrUpdate(custReplyVo.getAttributeId(), custReplyVo.getMvnoId());
				custReplyVo.setCreatedOn(customerReply.getCreatedOn());
				if(custReplyVo.getMvnoId() != null && custReplyVo.getMvnoId() == 1)
					custReplyVo.setMvnoId(customerReply.getMvnoId());
			}
			
			customerService.findCustomerById(custReplyVo.getCustomerId(), custReplyVo.getMvnoId());
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public CustomerReply updateCustomerReply(CustomerReply customerReply, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		
		try {
			customerReply.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validatCustomerReply(customerReply,true);
			customerReply.setLastModifiedOn(new Timestamp(new Date().getTime()));
			//log.info("Customer reply updated succefully, updated values "+customerReply.getCustomerId());
			return custReplyRepository.save(customerReply);
			
		}
		catch (Throwable e) {
		//	log.error("Error while updating customer reply: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public void deleteCustomerReply(Long id, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		
		try {
			
			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id to delete customer reply.");
			}
			validateCustomerReplyForDeleteOrUpdate(id, mvnoId);
			custReplyRepository.deleteById(id);
		//	log.info("Customer reply deleted succefully: "+id);
		}
		catch (RuntimeException e) {
//			log.error("Error while deleting customer reply: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	public  BooleanExpression findCustomerRepliesByCustomerId(Long customerId, Integer mvnoId) {
		
		QCustomerReply qCustomerReply = QCustomerReply.customerReply;
		BooleanExpression boolExp =  qCustomerReply.isNotNull();
		
		if (customerId != null) {
			boolExp = boolExp.and(qCustomerReply.customerId.eq(customerId));
		}
		
		return boolExp;
	}
}
