package com.savbill.radius.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.ConcurrentPolicy;
import com.savbill.radius.entity.QConcurrentPolicy;
import com.savbill.radius.helper.ConcurrentPolicyDto;
import com.savbill.radius.repository.ConcurrentPolicyRepository;
import com.savbill.radius.repository.CustomerRepository;
import com.savbill.radius.services.ConcurrentPolicyService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class ConcurrentPolicyServiceImpl implements ConcurrentPolicyService {

    @Autowired
    ConcurrentPolicyRepository concurrentPolicyRepository;

    @Autowired
    CustomerRepository customerRepository;

    private static final Logger log = LoggerFactory.getLogger(ConcurrentPolicyServiceImpl.class);

    @Override
    public List<ConcurrentPolicy> findAll(Integer mvnoId) {
	try {
	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression exp = qConcurrentPolicy.isNotNull();
	    if (mvnoId != null && mvnoId == 1)
		return concurrentPolicyRepository.findAll();
	    else {
		exp = exp.and(qConcurrentPolicy.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		return (List<ConcurrentPolicy>) concurrentPolicyRepository.findAll(exp);
	    }
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public ConcurrentPolicy findById(Long concurrentPolicyId, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(concurrentPolicyId))
		throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid policy id.");

	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qConcurrentPolicy.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    boolExp = boolExp.and(qConcurrentPolicy.concurrentPolicyId.eq(concurrentPolicyId));

	    Optional<ConcurrentPolicy> policyOptional = concurrentPolicyRepository.findOne(boolExp);

	    if (policyOptional.isPresent()) {
		return policyOptional.get();
	    } else {
		throw new IllegalArgumentException(
			"No record found with policy id : '" + concurrentPolicyId + "'. Please enter valid policy id");
	    }
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private ConcurrentPolicy findForDeleteAndUpdate(Long concurrentPolicyId, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(concurrentPolicyId))
		throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid policy id.");

	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qConcurrentPolicy.mvnoId.eq(mvnoId));
	    boolExp = boolExp.and(qConcurrentPolicy.concurrentPolicyId.eq(concurrentPolicyId));

	    Optional<ConcurrentPolicy> policyOptional = concurrentPolicyRepository.findOne(boolExp);

	    if (policyOptional.isPresent()) {
		return policyOptional.get();
	    } else {
		throw new IllegalArgumentException("You are not authorised to perform update/delete of this record.");
	    }
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private ConcurrentPolicy findByPolicyName(String name, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
		throw new IllegalArgumentException(
			RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid policy name.");

	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
	    boolExp = boolExp.and(qConcurrentPolicy.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qConcurrentPolicy.name.eq(name));

	    Optional<ConcurrentPolicy> policyOptional = concurrentPolicyRepository.findOne(boolExp);

	    if (policyOptional.isPresent()) {
		return policyOptional.get();
	    } else {
		throw new IllegalArgumentException(
			"No record found with policy name : '" + name + "'. Please enter valid policy name");
	    }
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<ConcurrentPolicy> searchByPolicyName(String policyName, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(policyName))
		throw new IllegalArgumentException(
			RadiusConstants.BASIC_STRING_MSG + "Please enter valid policy name.");

	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
	    boolExp = boolExp.and(qConcurrentPolicy.name.like("%" + policyName + "%"));
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qConcurrentPolicy.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

	    return (List<ConcurrentPolicy>) concurrentPolicyRepository.findAll(boolExp);

	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public ConcurrentPolicy add(ConcurrentPolicyDto concurrentPolicyDto, Integer mvnoId) {
	try {
	    ConcurrentPolicy concurrentPolicy = new ConcurrentPolicy(concurrentPolicyDto);
	    concurrentPolicy.setMvnoId(mvnoId);
	    validatePolicyData(concurrentPolicy, false);
	    return concurrentPolicyRepository.save(concurrentPolicy);
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validatePolicyData(ConcurrentPolicy concurrentPolicy, boolean isUpdate) {
	try {
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(concurrentPolicy.getName())) {
		throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid policy name");
	    } else if (!ValidateCrudTransactionData
		    .validateLongTypeFieldValue(concurrentPolicy.getNoOfConcurrentConnections())) {
		throw new IllegalArgumentException(
			RadiusConstants.BASIC_STRING_MSG + "Please enter valid no of concurrent connections");
	    } else if (!concurrentPolicy.getStatus().equals(RadiusConstants.ACTIVE)
		    && !concurrentPolicy.getStatus().equals(RadiusConstants.IN_ACTIVE)) {
		throw new IllegalArgumentException("Please enter valid status, sync status should be '"
			+ RadiusConstants.ACTIVE + "' OR '" + RadiusConstants.IN_ACTIVE + "'");
	    } else if (isUpdate) {
		ConcurrentPolicy policy = findByPolicyName(concurrentPolicy.getName(), concurrentPolicy.getMvnoId());
		concurrentPolicy.setConcurrentPolicyId(policy.getConcurrentPolicyId());
	    }
	    checkForUniqueName(concurrentPolicy, isUpdate);
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public ConcurrentPolicy update(ConcurrentPolicyDto concurrentPolicyDto, Integer mvnoId) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
	try {
	    ConcurrentPolicy concurrentPolicy = new ConcurrentPolicy(concurrentPolicyDto);
	    concurrentPolicy.setMvnoId(mvnoId);
	    validatePolicyData(concurrentPolicy, true);
	  //  log.info("Concurrent Policy updated succefully: " + concurrentPolicy.getName());
	    return concurrentPolicyRepository.save(concurrentPolicy);
	} catch (Throwable e) {
	   // log.error("Error while updating Concurrent Policy: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}
    }

    @Override
    public void delete(Long concurrentPolicyId, Integer mvnoId) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
	try {
	    ConcurrentPolicy concurrentPolicy = findForDeleteAndUpdate(concurrentPolicyId, mvnoId);
	    concurrentPolicyRepository.delete(concurrentPolicy);
	  //  log.info("Concurrent Policy deleted succefully: " + concurrentPolicy.getName());

	} catch (Throwable e) {
	   // log.error("Error while deleting Concurrent Policy: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}

    }

    private void checkForUniqueName(ConcurrentPolicy concurrentPolicy, boolean isUpdate) {
	try {
	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
	    String msg = "Concurrent policy already exist with name : '" + concurrentPolicy.getName()
		    + "', Please enter unique concurrent policy name." + RadiusConstants.NOT_PUT_IN_QUEUE;

	    if (isUpdate) {
		boolExp = boolExp
			.and(qConcurrentPolicy.concurrentPolicyId.ne(concurrentPolicy.getConcurrentPolicyId()));
	    }
	    if (concurrentPolicy.getMvnoId() == 1) {
		boolExp = boolExp.and(qConcurrentPolicy.name.eq(concurrentPolicy.getName()));
		List<ConcurrentPolicy> concurrentPolicyList = (List<ConcurrentPolicy>) concurrentPolicyRepository
			.findAll(boolExp);
		if (!concurrentPolicyList.isEmpty()) {
		    throw new IllegalArgumentException(msg);
		}
	    } else {
		boolExp = boolExp.and(qConcurrentPolicy.name.eq(concurrentPolicy.getName())).and(
			(qConcurrentPolicy.mvnoId.eq(concurrentPolicy.getMvnoId())).or(qConcurrentPolicy.mvnoId.eq(1)));
		Optional<ConcurrentPolicy> optionalConcurrentPolicy = concurrentPolicyRepository.findOne(boolExp);
		if (optionalConcurrentPolicy.isPresent()) {
		    throw new IllegalArgumentException(msg);
		}
	    }
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public String changePolicyStatus(Long concurrentPolicyId, String status, Integer mvnoId) {
	try {
	    ConcurrentPolicy concurrentPolicy = findForDeleteAndUpdate(concurrentPolicyId, mvnoId);
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
		throw new RuntimeException("Concurrent Policy status is mandatory. Please enter valid status.");
	    } else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) {
		throw new IllegalArgumentException("Please enter valid Concurrent Policy status. It should be '"
			+ RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
	    }

	    concurrentPolicy.setStatus(status);
	    concurrentPolicyRepository.save(concurrentPolicy);
	    String msg = "";
	    if (status.equals(RadiusConstants.ACTIVE)) {
		msg = "Concurrent Policy '" + concurrentPolicy.getName() + "' has been activated successfully.";
	    } else {
		msg = "Concurrent Policy '" + concurrentPolicy.getName() + "' has been inactivated successfully.";
	    }
	    return msg;
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<ConcurrentPolicy> getConcurrentPolicies(Integer mvnoId) {
	try {
	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression exp = qConcurrentPolicy.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		exp = exp.and(qConcurrentPolicy.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    exp = exp.and(qConcurrentPolicy.status.eq(RadiusConstants.ACTIVE));
	    return (List<ConcurrentPolicy>) concurrentPolicyRepository.findAll(exp);
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}

    }

    @Override
    public List<ConcurrentPolicy> findByNameAndMvnoId(String policyName, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(policyName))
		throw new IllegalArgumentException(
			RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid policy name.");

	    QConcurrentPolicy qConcurrentPolicy = QConcurrentPolicy.concurrentPolicy;
	    BooleanExpression boolExp = qConcurrentPolicy.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qConcurrentPolicy.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    boolExp = boolExp.and(qConcurrentPolicy.name.eq(policyName));

	    return (List<ConcurrentPolicy>) concurrentPolicyRepository.findAll(boolExp);
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

}
