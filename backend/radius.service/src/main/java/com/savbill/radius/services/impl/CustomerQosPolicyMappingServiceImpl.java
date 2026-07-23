package com.savbill.radius.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.CustomerQosPolicyMapping;
import com.savbill.radius.entity.QCustomerQosPolicyMapping;
import com.savbill.radius.repository.CustomerQosPolicyMappingRepository;
import com.savbill.radius.services.CustomerQosPolicyMappingService;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class CustomerQosPolicyMappingServiceImpl implements CustomerQosPolicyMappingService{

	@Autowired
	private CustomerQosPolicyMappingRepository qosPolicyMappingRepository;

	@Autowired
	private CustomerService customerService;

	private static final Logger log = LoggerFactory.getLogger(CustomerQosPolicyMappingServiceImpl.class);

	@Override
	public CustomerQosPolicyMapping addQosPolicyMapping(CustomerQosPolicyMapping qosPolicyMapping, Integer validateMvnoId) {
		try {

			qosPolicyMapping.setMvnoId(ValidateCrudTransactionData.validateMvnoId(validateMvnoId));
			validateQosPolicyMapping(qosPolicyMapping, false);
			return qosPolicyMappingRepository.save(qosPolicyMapping);
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void validateQosPolicyMapping(CustomerQosPolicyMapping qosPolicyMappingVo, boolean isUpdate) {
		try {

			if (isUpdate) {

				if (qosPolicyMappingVo.getQosPolicyMappingId() == null || qosPolicyMappingVo.getQosPolicyMappingId() == 0) {
					throw new IllegalArgumentException("Please enter valid qos Policy Mapping id to update client reply.");
				}
				CustomerQosPolicyMapping qosPolicyMapping = validateQosPolicyMappingForDeleteOrUpdate(qosPolicyMappingVo.getQosPolicyMappingId(),
						qosPolicyMappingVo.getMvnoId());
				if (qosPolicyMappingVo.getMvnoId() != null && qosPolicyMappingVo.getMvnoId() == 1)
					qosPolicyMappingVo.setMvnoId(qosPolicyMapping.getMvnoId());
			}

			Long custId = Optional.ofNullable(qosPolicyMappingVo.getCustId()).filter(
					customerId -> (qosPolicyMappingVo.getCustId() != 0 && qosPolicyMappingVo.getCustId() != null))
					.orElseThrow(() -> new RuntimeException("Please enter valid customer id."));
			Long qosFrom = Optional.ofNullable(qosPolicyMappingVo.getQosFrom())
					.filter(qosF -> (qosPolicyMappingVo.getQosFrom() != null	&& qosPolicyMappingVo.getQosFrom() != 0 ))
					.orElseThrow(() -> new RuntimeException("Please enter valid Qos From."));
			Long qosTo = Optional.ofNullable(qosPolicyMappingVo.getQosTo())
					.filter(qosF -> (qosPolicyMappingVo.getQosTo() != null	&& qosPolicyMappingVo.getQosTo() != 0 ))
					.orElseThrow(() -> new RuntimeException("Please enter valid Qos To."));
			Long uploadQos = Optional.ofNullable(qosPolicyMappingVo.getUploadQos())
					.filter(qosF -> (qosPolicyMappingVo.getUploadQos() != null	&& qosPolicyMappingVo.getUploadQos() != 0 ))
					.orElseThrow(() -> new RuntimeException("Please enter valid upload Qos."));
			Long downloadQos = Optional.ofNullable(qosPolicyMappingVo.getDownloadQos())
					.filter(qosF -> (qosPolicyMappingVo.getDownloadQos() != null	&& qosPolicyMappingVo.getDownloadQos() != 0 ))
					.orElseThrow(() -> new RuntimeException("Please enter valid Download Qos."));


			customerService.findCustomerById(qosPolicyMappingVo.getCustId(), qosPolicyMappingVo.getMvnoId());
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}

	}
	
	@Override
    public void deleteByCustId(Long custId, Integer mvnoId) {
	try {
	    QCustomerQosPolicyMapping qCustomerQosPolicyMapping = QCustomerQosPolicyMapping.customerQosPolicyMapping;
	    BooleanExpression boolExp = qCustomerQosPolicyMapping.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qCustomerQosPolicyMapping.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qCustomerQosPolicyMapping.custId.eq(custId));
	    List<CustomerQosPolicyMapping> qosPolicyMappingList = (List<CustomerQosPolicyMapping>) qosPolicyMappingRepository.findAll(boolExp);

	    if (qosPolicyMappingList.size() > 0) {
	    	qosPolicyMappingRepository.deleteAll(qosPolicyMappingList);
	    }

	} catch (Exception e) {
	    //log.error("Error while deleting Qos Policy Mapping: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	}
    }

	@Override
	public List<CustomerQosPolicyMapping> findAllQosPolicyMappings(Integer mvnoId) {
		try {
			QCustomerQosPolicyMapping qCustomerQosPolicyMapping = QCustomerQosPolicyMapping.customerQosPolicyMapping;
			BooleanExpression exp = qCustomerQosPolicyMapping.isNotNull();
			if (mvnoId != null && mvnoId == 1)
				return qosPolicyMappingRepository.findAll();
			else {
				exp = exp.and(qCustomerQosPolicyMapping.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<CustomerQosPolicyMapping>) qosPolicyMappingRepository.findAll(exp);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public CustomerQosPolicyMapping findQosPolicyMappingById(Long qosPolicyMappingId, Integer mvnoId) {
		try {

			if (qosPolicyMappingId == null || qosPolicyMappingId == 0) {
				throw new IllegalArgumentException("Please enter valid qos Policy Mapping Id.");
			}
			QCustomerQosPolicyMapping qCustomerQosPolicyMapping = QCustomerQosPolicyMapping.customerQosPolicyMapping;
			BooleanExpression boolExp = qCustomerQosPolicyMapping.isNotNull();
			if (mvnoId == null || mvnoId != 1)
				boolExp = boolExp
						.and(qCustomerQosPolicyMapping.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qCustomerQosPolicyMapping.qosPolicyMappingId.eq(qosPolicyMappingId));

			Optional<CustomerQosPolicyMapping> qosPolicyMapping = qosPolicyMappingRepository.findOne(boolExp);

			if (!qosPolicyMapping.isPresent()) {
				throw new IllegalArgumentException("No record found with qos Policy Mapping id : '" + qosPolicyMappingId
						+ "' ,Please enter valid qos Policy Mapping id.");
			}

			return qosPolicyMapping.get();
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<CustomerQosPolicyMapping> findQosPolicyMappingByCustId(Long custId, Integer mvnoId) {
		try {

			if (custId == null || custId == 0) {
				throw new IllegalArgumentException("Please enter valid Customer Id.");
			}
			QCustomerQosPolicyMapping qCustomerQosPolicyMapping = QCustomerQosPolicyMapping.customerQosPolicyMapping;
			BooleanExpression exp = qCustomerQosPolicyMapping.isNotNull();
			exp = exp.and(qCustomerQosPolicyMapping.custId.eq(custId));
			if (mvnoId == null || mvnoId != 1)
				exp = exp.and(qCustomerQosPolicyMapping.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<CustomerQosPolicyMapping>) qosPolicyMappingRepository.findAll(exp);
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public CustomerQosPolicyMapping updateQosPolicyMapping(CustomerQosPolicyMapping qosPolicyMapping, Integer mvnoId) {
		try {
			qosPolicyMapping.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateQosPolicyMapping(qosPolicyMapping, true);
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
			//log.info("Qos Policy Mapping updated succefully, updated values " + qosPolicyMapping.getCustId());
			return qosPolicyMappingRepository.save(qosPolicyMapping);

		} catch (Throwable e) {
		//	log.error("Error while updating Qos Policy Mapping: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public void deleteQosPolicyMapping(Long qosPolicyMappingId, Integer mvnoId) {
		try {

			if (qosPolicyMappingId == null || qosPolicyMappingId == 0) {
				throw new IllegalArgumentException(
						"Please enter valid qos Policy Mapping Id to delete qos Policy Mapping Id.");
			}
			validateQosPolicyMappingForDeleteOrUpdate(qosPolicyMappingId, mvnoId);
			qosPolicyMappingRepository.deleteById(qosPolicyMappingId);
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
			//log.info("Qos Policy Mapping deleted succefully: " + qosPolicyMappingId);
		} catch (RuntimeException e) {
			//log.error("Error while deleting client reply: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	private CustomerQosPolicyMapping validateQosPolicyMappingForDeleteOrUpdate(Long qosPolicyMappingId, Integer mvnoId) {
		try {

			if (qosPolicyMappingId == null || qosPolicyMappingId == 0) {
				throw new IllegalArgumentException("Please enter valid qos policy mapping id.");
			}
			QCustomerQosPolicyMapping qCustomerQosPolicyMapping = QCustomerQosPolicyMapping.customerQosPolicyMapping;
			BooleanExpression boolExp = qCustomerQosPolicyMapping.isNotNull();
			if (mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qCustomerQosPolicyMapping.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qCustomerQosPolicyMapping.qosPolicyMappingId.eq(qosPolicyMappingId));

			Optional<CustomerQosPolicyMapping> qosPolicyMapping = qosPolicyMappingRepository.findOne(boolExp);

			if (!qosPolicyMapping.isPresent()) {
				throw new IllegalArgumentException("You do not have access to update or delete this record.");
			}

			return qosPolicyMapping.get();
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

}
