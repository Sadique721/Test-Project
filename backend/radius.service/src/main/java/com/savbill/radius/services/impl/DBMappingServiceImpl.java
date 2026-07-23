package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.entity.QDBMapping;
import com.savbill.radius.helper.DBMappingDto;
import com.savbill.radius.repository.DBMappingRepository;
import com.savbill.radius.services.DBMappingService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.servlet.http.HttpServletRequest;

@Service
public class DBMappingServiceImpl implements DBMappingService {

    @Autowired
    private DBMappingRepository dbMappingRepository;
	@Autowired
	UpdateDiffFinder updateDiffFinder;

    private static final Logger log = LoggerFactory.getLogger(DBMappingServiceImpl.class);

    @Override
    public DBMapping saveDBMapping(DBMappingDto dbMappingDto, Integer mvnoId) {
	try {
	    DBMapping dbMapping = new DBMapping(dbMappingDto);
	    dbMapping.setMvnoId(mvnoId);
	    validateDBMappingData(dbMapping, false);
	    dbMapping.setCreatedOn(new Timestamp(new Date().getTime()));
	    dbMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
	    return dbMappingRepository.save(dbMapping);
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<DBMapping> findAllDbMappings(Integer mvnoId) {
	try {
	    QDBMapping qdbMapping = QDBMapping.dBMapping;
	    BooleanExpression exp = qdbMapping.isNotNull();
	    if (mvnoId != null && mvnoId == 1)
		return dbMappingRepository.findAll();
	    else {
		exp = exp.and(qdbMapping.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		return (List<DBMapping>) dbMappingRepository.findAll(exp);
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<DBMapping> findDBMappingByDBMappingMasterId(Long dbMappingMasterId, Integer mvnoId) {
	try {
	    validateDBMappingByDBMappingMasterId(dbMappingMasterId);
	    QDBMapping qdbMapping = QDBMapping.dBMapping;
	    BooleanExpression boolExp = qdbMapping.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qdbMapping.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    boolExp = boolExp.and(qdbMapping.mappingMasterId.eq(dbMappingMasterId));

	    List<DBMapping> dbMappingList = (List<DBMapping>) dbMappingRepository.findAll(boolExp);
	    return dbMappingList;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateDBMappingByDBMappingMasterId(Long dbMappingMasterId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(dbMappingMasterId)) {
		throw new IllegalArgumentException("Please enter valid DB Mapping id.");
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    @Transactional
		public List<DBMapping> updateDBMapping(List<DBMapping> dbMappingList, Long mappingMasterId, Integer mvnoId, HttpServletRequest request) {
	try {
		List<DBMapping> dbMappingMasters = dbMappingRepository.findDBMappingByMappingMasterId(mappingMasterId);
	    deleteOldDBMapping(mappingMasterId, mvnoId);
	    saveChangedDBMapping(dbMappingList, mvnoId);
		String updated=null;
		for(int i=0;i<=dbMappingList.size()-1;i++ ){
			updated=updateDiffFinder.getUpdatedDiff(dbMappingMasters.get(i),dbMappingList.get(i) );
		}
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "DbMapping has been updated successfully , updated values,"+updated+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return dbMappingRepository.findAll();
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void deleteOldDBMapping(Long mappingMasterId, Integer mvnoId) {
	List<DBMapping> dbMappingMastersToDelete = new ArrayList<DBMapping>();
	List<DBMapping> dbMappingMasters = dbMappingRepository.findDBMappingByMappingMasterId(mappingMasterId);
	if (!dbMappingMasters.isEmpty()) {
	    for (DBMapping dbMapping : dbMappingMasters) {
		if (dbMapping.getMvnoId() == mvnoId) {
		    dbMappingMastersToDelete.add(dbMapping);
		}
	    }
	}
	dbMappingRepository.deleteAll(dbMappingMastersToDelete);
    }

    private void saveChangedDBMapping(List<DBMapping> dbMappingList, Integer mvnoId) {

	List<DBMapping> dbMappingListToSave = new ArrayList<DBMapping>();
	if (!dbMappingList.isEmpty()) {
	    for (DBMapping mapping : dbMappingList) {
		mapping.setCreatedOn(new Timestamp(new Date().getTime()));
		mapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
		mapping.setMappingMasterId(dbMappingList.get(0).getMappingMasterId());
		mapping.setMvnoId(mvnoId);
		validateDBMappingData(mapping, false);
		dbMappingListToSave.add(mapping);
	    }
	}
	dbMappingRepository.saveAll(dbMappingListToSave);
    }

    private void validateDBMappingData(DBMapping dbMapping, boolean isUpdate) {
	if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMapping.getRadiusName())) {
	    throw new IllegalArgumentException(
		    "DB Mapping Radius Name is mandatory. Please enter valid DB Mapping Radius Name.");
	} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(dbMapping.getMappingMasterId())) {
	    throw new IllegalArgumentException(
		    "DB mapping master id is mandatory. Please enter valid DB mapping master id.");
	} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMapping.getDbColumnName())) {
	    throw new IllegalArgumentException("DB column name is mandatory. Please enter valid DB column name.");
	}
    }

    @Override
    public void deleteDBMappingByMappingId(Long dbMappingId, Integer mvnoId) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
	try {
	    validateDBMappingById(dbMappingId, mvnoId);
	    dbMappingRepository.deleteById(dbMappingId);
	   // log.info("DB Mapping deleted successfully: " + dbMappingId);
	} catch (RuntimeException e) {
	   // log.error("Error while deleting DB Mapping: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}
    }

    @Override
    public void deleteByMappingMasterId(Long dbMappingMasterId, Integer mvnoId) {
	try {
	    QDBMapping qdbMapping = QDBMapping.dBMapping;
	    BooleanExpression boolExp = qdbMapping.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qdbMapping.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qdbMapping.mappingMasterId.eq(dbMappingMasterId));
	    List<DBMapping> dbMappingList = (List<DBMapping>) dbMappingRepository.findAll(boolExp);

	    if (dbMappingList.size() > 0) {
		dbMappingRepository.deleteAll(dbMappingList);
	    }

	} catch (Exception e) {
	  //  log.error("Error while deleting DB Mapping: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	}
    }

    private Optional<DBMapping> validateDBMappingById(Long id, Integer mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
		throw new IllegalArgumentException("Please enter valid DB Mapping id.");
	    QDBMapping qdbMapping = QDBMapping.dBMapping;
	    BooleanExpression boolExp = qdbMapping.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qdbMapping.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qdbMapping.mappingId.eq(id));
	    Optional<DBMapping> dbMapping = dbMappingRepository.findOne(boolExp);

	    if (!dbMapping.isPresent()) {
		throw new IllegalArgumentException("No record found for DB Mapping with id : '" + id
			+ "'. Or you are not authorised to update/delete this record.");
	    }

	    return dbMapping;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }
}
