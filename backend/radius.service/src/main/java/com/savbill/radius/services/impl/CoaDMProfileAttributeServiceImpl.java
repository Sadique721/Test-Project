package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savbill.radius.entity.CoaDMProfileAttribute;
import com.savbill.radius.entity.QCoaDMProfileAttribute;
import com.savbill.radius.helper.CoaDMProfileAttributeDto;
import com.savbill.radius.repository.CoaDMProfileAttributeRepository;
import com.savbill.radius.services.CoaDMProfileAttributeService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class CoaDMProfileAttributeServiceImpl implements CoaDMProfileAttributeService {

    @Autowired
    private CoaDMProfileAttributeRepository coaDMProfileAttributeRepository;

    private static final Logger log = LoggerFactory.getLogger(CoaDMProfileAttributeServiceImpl.class);

    @Override
    public List<CoaDMProfileAttribute> findCoaDMProfileAttributeByCoaDMProfileId(Long coaDMProfileId, Integer mvnoId) {
	try {
	    validateCoaDMProfileAttrByCoaDMProfileId(coaDMProfileId);
	    QCoaDMProfileAttribute qCoaDMProfileAttributeAttribute = QCoaDMProfileAttribute.coaDMProfileAttribute;
	    BooleanExpression exp = qCoaDMProfileAttributeAttribute.isNotNull();
	    exp = exp.and(qCoaDMProfileAttributeAttribute.coaDMProfileId.eq(coaDMProfileId));
	    if (mvnoId == null || mvnoId != 1)
		exp = exp.and(qCoaDMProfileAttributeAttribute.mvnoId
			.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    return (List<CoaDMProfileAttribute>) coaDMProfileAttributeRepository.findAll(exp);
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<CoaDMProfileAttribute> findAllCoaDMProfileAttributes(Integer mvnoId) {
	try {
	    QCoaDMProfileAttribute qCoaDmProfileAttribute = QCoaDMProfileAttribute.coaDMProfileAttribute;
	    BooleanExpression exp = qCoaDmProfileAttribute.isNotNull();
	    if (mvnoId != null && mvnoId == 1)
		return coaDMProfileAttributeRepository.findAll();
	    else {
		exp = exp.and(qCoaDmProfileAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		return (List<CoaDMProfileAttribute>) coaDMProfileAttributeRepository.findAll(exp);
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public void deleteCoaDMProfileAttributeById(Long id, Integer mvnoid) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
	try {
	    validateCoaDMProfileAttrToDeleteOrUpdate(id, ValidateCrudTransactionData.validateMvnoId(mvnoid));
	    coaDMProfileAttributeRepository.deleteById(id);
	  //  log.info("COA DM Profile Attribute deleted succefully: " + id);
	} catch (RuntimeException e) {
	//    log.error("Error while deleting COA DM Profile Attribute: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}
    }

    @Override
    public CoaDMProfileAttribute saveCoaDMProfileAttribute(CoaDMProfileAttributeDto coaDMProfileAttributeDto,
	    Integer mvnoId) {
	try {
	    CoaDMProfileAttribute coaDMProfileAttribute = new CoaDMProfileAttribute(coaDMProfileAttributeDto);
	    coaDMProfileAttribute.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
	    validateProfileAttributData(coaDMProfileAttribute, false);
	    coaDMProfileAttribute.setCreatedOn(new Timestamp(new Date().getTime()));
	    coaDMProfileAttribute.setLastModifiedOn(new Timestamp(new Date().getTime()));
	    return coaDMProfileAttributeRepository.save(coaDMProfileAttribute);
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    @Transactional
    public List<CoaDMProfileAttribute> updateCoaDMProfileAttribute(
	    List<CoaDMProfileAttribute> coaDMProfileAttributeList, Integer mvnoid, Long coaDMId) {
	try {
		if(coaDMProfileAttributeList.size() == 0)
			deleteCoaDMProfileAttribute(coaDMId,mvnoid);
		else {
			deleteCoaDMProfileAttribute(coaDMProfileAttributeList.get(0).getCoaDMProfileId(), mvnoid);
			saveCoaDMProfileAttribute(coaDMProfileAttributeList, mvnoid);
		}
	    return coaDMProfileAttributeRepository.findAll();
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void deleteCoaDMProfileAttribute(Long coaDMProfileId, Integer mvnoid) {
	List<CoaDMProfileAttribute> caoProfileAttributeListTodelete = new ArrayList<CoaDMProfileAttribute>();
	List<CoaDMProfileAttribute> caoProfileAttributeList = coaDMProfileAttributeRepository.findCoaDMProfileAttributeByCoaDMProfileId(coaDMProfileId);
	if(!caoProfileAttributeList.isEmpty())
	{
	    for (CoaDMProfileAttribute coaDMProfileAttribute : caoProfileAttributeList) {
		if(coaDMProfileAttribute.getMvnoId() == mvnoid)
		{
		    caoProfileAttributeListTodelete.add(coaDMProfileAttribute);
		}
	    }
	}
	coaDMProfileAttributeRepository.deleteAll(caoProfileAttributeListTodelete);
    }

    private void saveCoaDMProfileAttribute(
	    List<CoaDMProfileAttribute> caoProfileAttributeList, Integer mvnoid) {
	List<CoaDMProfileAttribute> caoProfileAttributeListToSave = new ArrayList<CoaDMProfileAttribute>();
	if (!caoProfileAttributeList.isEmpty()) {
	    for (CoaDMProfileAttribute coaDMProfileAttribute : caoProfileAttributeList) {
		coaDMProfileAttribute.setCreatedOn(new Timestamp(new Date().getTime()));
		coaDMProfileAttribute.setLastModifiedOn(new Timestamp(new Date().getTime()));
		coaDMProfileAttribute.setCoaDMProfileId(caoProfileAttributeList.get(0).getCoaDMProfileId());
		coaDMProfileAttribute.setMvnoId(mvnoid);
		validateProfileAttributData(coaDMProfileAttribute, false);
		caoProfileAttributeListToSave.add(coaDMProfileAttribute);
	    }
	}
	coaDMProfileAttributeRepository.saveAll(caoProfileAttributeListToSave);
    }

    private void validateCoaDMProfileAttrByCoaDMProfileId(Long coaDMProfileId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(coaDMProfileId)) {
		throw new IllegalArgumentException("Please enter valid COA/DM Profile id.");
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private Optional<CoaDMProfileAttribute> validateCoaDMProfileAttrById(Long id, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
		throw new IllegalArgumentException("Please enter valid COA/DM Profile attribute id.");

	    QCoaDMProfileAttribute qCoaDMProfileAttribute = QCoaDMProfileAttribute.coaDMProfileAttribute;
	    BooleanExpression boolExp = qCoaDMProfileAttribute.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qCoaDMProfileAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    boolExp = boolExp.and(qCoaDMProfileAttribute.coaDMProfileAttributeMappingId.eq(id));
	    Optional<CoaDMProfileAttribute> coaDMProfileAttribute = coaDMProfileAttributeRepository.findOne(boolExp);

	    if (!coaDMProfileAttribute.isPresent()) {
		throw new IllegalArgumentException("No record found for COA/DM Profile attribute with id : '" + id
			+ "'. Please enter valid COA/DM Profile attribute id.");
	    }

	    return coaDMProfileAttribute;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private Optional<CoaDMProfileAttribute> validateCoaDMProfileAttrToDeleteOrUpdate(Long id, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
		throw new IllegalArgumentException("Please enter valid COA/DM Profile attribute id.");

	    QCoaDMProfileAttribute qCoaDMProfileAttribute = QCoaDMProfileAttribute.coaDMProfileAttribute;
	    BooleanExpression boolExp = qCoaDMProfileAttribute.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qCoaDMProfileAttribute.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qCoaDMProfileAttribute.coaDMProfileAttributeMappingId.eq(id));
	    Optional<CoaDMProfileAttribute> coaDMProfileAttribute = coaDMProfileAttributeRepository.findOne(boolExp);

	    if (!coaDMProfileAttribute.isPresent()) {
		throw new IllegalArgumentException("You do not have access to update or delete this record");
	    }

	    return coaDMProfileAttribute;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateProfileAttributData(CoaDMProfileAttribute coaDMProfileAttribute, boolean isUpdate) {
	if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfileAttribute.getRadiusAtt())) {
	    throw new IllegalArgumentException(
		    "COA/DM Profile Radius Attribute is mandatory. Please enter valid COA/DM Profile Radius Attribute.");
	} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(coaDMProfileAttribute.getCoaDMProfileId())) {
	    throw new IllegalArgumentException("COA/DM Profile Id is mandatory. Please enter valid COA/DM Profile Id.");
	} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfileAttribute.getProfileAtt())) {
	    throw new IllegalArgumentException(
		    "Profile Attribute is mandatory. Please enter valid COA/DM Profile Attribute.");
	}
    }

    @Override
    public void deleteCoaDMProfileAttributeByCoaDmProfileId(Long coaDMProfileId, Integer mvnoId) {
	try {
	    QCoaDMProfileAttribute qCoaDMProfileAttribute = QCoaDMProfileAttribute.coaDMProfileAttribute;
	    BooleanExpression boolExp = qCoaDMProfileAttribute.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qCoaDMProfileAttribute.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qCoaDMProfileAttribute.coaDMProfileId.eq(coaDMProfileId));
	    List<CoaDMProfileAttribute> coaDMProfileAttributeList = (List<CoaDMProfileAttribute>) coaDMProfileAttributeRepository
		    .findAll(boolExp);

	    if (coaDMProfileAttributeList.size() > 0) {
		coaDMProfileAttributeRepository.deleteAll(coaDMProfileAttributeList);
	    }

	} catch (Exception e) {
	  //  log.error("Error while deleting DB Mapping: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	}
    }

}
