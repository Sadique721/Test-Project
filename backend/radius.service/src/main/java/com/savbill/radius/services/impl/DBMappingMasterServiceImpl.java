package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.entity.DBMappingMaster;
import com.savbill.radius.repository.DBMappingRepository;
import com.savbill.radius.utils.*;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.DBMappingDto;
import com.savbill.radius.helper.DBMappingMasterDto;
import com.savbill.radius.repository.DBMappingMasterRepository;
import com.savbill.radius.repository.RadiusProfileRepository;
import com.savbill.radius.services.DBMappingMasterService;
import com.savbill.radius.services.DBMappingService;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.servlet.http.HttpServletRequest;

@Service
public class DBMappingMasterServiceImpl implements DBMappingMasterService {

    @Autowired
    private DBMappingMasterRepository dBMappingMasterRepository;
    @Autowired
    private DBMappingService dbMappingService;
    @Autowired
    private RadiusProfileRepository radiusProfileRepository;
    @Autowired
    private DBMappingRepository dbMappingRepository;
	@Autowired
	private UpdateDiffFinder updateDiffFinder;
    private static final Logger log = LoggerFactory.getLogger(DBMappingMasterServiceImpl.class);

    @Override
    public List<DBMappingMaster> findAllDBMappingMasters(Integer mvnoId) {
	try {
	    QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
	    BooleanExpression exp = qdbMappingMaster.isNotNull();
	    if (mvnoId != null && mvnoId == 1)
		return dBMappingMasterRepository.findAll();
	    else {
		exp = exp.and(qdbMappingMaster.mvnoId.in(mvnoId, 1));
		return (List<DBMappingMaster>) dBMappingMasterRepository.findAll(exp);
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public DBMappingMaster findDBMappingMasterById(Long dbMapingMastersId, Integer mvnoId) {
	try {
	    QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
	    BooleanExpression boolExp = qdbMappingMaster.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qdbMappingMaster.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    boolExp = boolExp.and(qdbMappingMaster.mappingMasterId.eq(dbMapingMastersId));
	    Optional<DBMappingMaster> dbMappingMaster = dBMappingMasterRepository.findOne(boolExp);

	    if (!dbMappingMaster.isPresent()) {
		throw new IllegalArgumentException("No record found for DB Mapping master with id : '"
			+ dbMapingMastersId + "'. Or you are not authorised to update/delete this record.");
	    }
	    return dbMappingMaster.get();
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /*
     * public DBMappingMaster findDuplicate(Long dbMapingMastersId, Integer mvnoId)
     * { try { QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
     * BooleanExpression boolExp = qdbMappingMaster.isNotNull(); boolExp =
     * boolExp.and(qdbMappingMaster.mvnoId.eq(ValidateCrudTransactionData.
     * validateMvnoId(mvnoId))); boolExp =
     * boolExp.and(qdbMappingMaster.mappingMasterId.eq(dbMapingMastersId));
     * Optional<DBMappingMaster> dbMappingMaster =
     * dBMappingMasterRepository.findOne(boolExp);
     *
     * if(!dbMappingMaster.isPresent()) { throw new
     * IllegalArgumentException("No record found for DB Mapping master with id : '"
     * +dbMapingMastersId+"'. Or you are not authorised to update/delete this record."
     * ); } return dbMappingMaster.get(); } catch (RuntimeException e) { throw new
     * RuntimeException(e.getMessage()); } }
     */
    @Override
    public List<DBMappingMaster> findDBMappingMastersByName(String name, Integer mvnoId) {
	try {
//	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
//		throw new IllegalArgumentException(
//			RadiusConstants.BASIC_STRING_MSG + "Please enter valid policy name.");

	    QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
	    BooleanExpression boolExp = qdbMappingMaster.isNotNull();
		if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")){
			return (List<DBMappingMaster>) dBMappingMasterRepository.findAll(boolExp);
		}
	    boolExp = boolExp.and(qdbMappingMaster.name.like("%" + name + "%"));
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp
			.and(qdbMappingMaster.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

	    List dbMappingMasterList = (List<DBMappingMaster>) dBMappingMasterRepository.findAll(boolExp);
//	    if(dbMappingMasterList.isEmpty())
//		{
//			throw new IllegalArgumentException(
//					"No record found by with name: "+name+" Please enter valid name.");
//		}
	    return dbMappingMasterList;
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private List<DBMappingMaster> findForUpdateAndDelete(DBMappingMaster dbMappingMaster, Boolean isUpdate) {
	try {
	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMappingMaster.getName()))
		throw new IllegalArgumentException(
			RadiusConstants.BASIC_STRING_MSG + "Please enter valid policy name.");

	    checkForUniqueName(dbMappingMaster, isUpdate);
			QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
			BooleanExpression boolExp = qdbMappingMaster.isNotNull();
			if(dbMappingMaster.getMvnoId() == null || dbMappingMaster.getMvnoId() != 1)
		boolExp = boolExp.and(qdbMappingMaster.mvnoId.in(dbMappingMaster.getMvnoId(), 1));
	    boolExp = boolExp.and(qdbMappingMaster.name.eq(dbMappingMaster.getName()));
	    return (List<DBMappingMaster>) dBMappingMasterRepository.findAll(boolExp);

	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void checkForUniqueName(DBMappingMaster dbMappingMaster, boolean isUpdate)
	{
		try
		{
			QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
			BooleanExpression boolExp = qdbMappingMaster.isNotNull();
			String msg = "DB Mapping master already exist with name : '"+dbMappingMaster.getName()+"', Please enter unique DB Mapping master name."+RadiusConstants.NOT_PUT_IN_QUEUE;

			if (isUpdate)
			{
				boolExp = boolExp.and(qdbMappingMaster.mappingMasterId.ne(dbMappingMaster.getMappingMasterId()));
			}
			if(dbMappingMaster.getMvnoId() == 1)
			{
				boolExp = boolExp.and(qdbMappingMaster.name.eq(dbMappingMaster.getName()));
				List<DBMappingMaster> dbMappingMasterList = (List<DBMappingMaster>) dBMappingMasterRepository.findAll(boolExp);
				if(!dbMappingMasterList.isEmpty())
				{
					throw new IllegalArgumentException(msg);
				}
			}
			else
			{
				boolExp = boolExp.and(qdbMappingMaster.name.eq(dbMappingMaster.getName())).and((qdbMappingMaster.mvnoId.eq(dbMappingMaster.getMvnoId())).or(qdbMappingMaster.mvnoId.eq(1)));
				Optional<DBMappingMaster> optionalDBMappingMaster = dBMappingMasterRepository.findOne(boolExp);
				if(optionalDBMappingMaster.isPresent())
				{
					throw new IllegalArgumentException(msg);
				}
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}


	@Override
	public DBMappingMaster saveDbMappingMaster(DBMappingMasterDto dBMappingMasterDto, Integer mvnoId) {
		 try {
			 DBMappingMaster dbMappingMaster = new DBMappingMaster(dBMappingMasterDto);
			 dbMappingMaster.setMvnoId(mvnoId);
	            validateDBMappingMasterData(dbMappingMaster,false);
	            dbMappingMaster.setCreatedOn(new Timestamp(new Date().getTime()));
	            dbMappingMaster.setLastModifiedOn(new Timestamp(new Date().getTime()));
	            if(dBMappingMasterDto.getDbMappingDtoList() != null){
	                for(DBMappingDto dbMappingDto : dBMappingMasterDto.getDbMappingDtoList()){
	                    DBMapping dbMapping = new DBMapping(dbMappingDto);
	                    dbMapping.setMvnoId(mvnoId);
	                    validateDBMappingData(dbMapping, false);
	                }
	            }
	            DBMappingMaster save = dBMappingMasterRepository.save(dbMappingMaster);
	            if(dBMappingMasterDto.getDbMappingDtoList() != null){
	                for(DBMappingDto dbMappingDto : dBMappingMasterDto.getDbMappingDtoList()){

		    dbMappingDto.setMappingMasterId(save.getMappingMasterId());
		    dbMappingService.saveDBMapping(dbMappingDto, mvnoId);
		}
	    }
	    return save;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateDBMappingData(DBMapping dbMapping, boolean isUpdate) {
	if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMapping.getRadiusName())) {
	    throw new IllegalArgumentException(
		    "DB Mapping Master Radius Attribute is mandatory. Please enter valid DB Mapping Master Radius Attribute.");
	} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMapping.getDbColumnName())) {
	    throw new IllegalArgumentException("DB Column name is mandatory. Please enter valid DB Column name.");
	}
    }

    @Override
    public DBMappingMaster updateDBMappingMaster(DBMappingMaster dBMappingMaster, Integer mvnoId, HttpServletRequest request) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
	try {
	    Optional<DBMappingMaster> oldDBMappingMaster = validateDBMappingMasterById(
		    dBMappingMaster.getMappingMasterId(), mvnoId, true);
		if(!oldDBMappingMaster.isPresent())
			throw new IllegalArgumentException(
					"No record found for  DB Mapping Master with  DB Mapping Master' Or you do not have access to update/delete this record");
	    if (mvnoId != 1)
		dBMappingMaster.setMvnoId(mvnoId);
	    else
		dBMappingMaster.setMvnoId(oldDBMappingMaster.get().getMvnoId());
	    validateDBMappingMasterData(dBMappingMaster, true);
	    dBMappingMaster.setCreatedOn(oldDBMappingMaster.get().getCreatedOn());
	    dBMappingMaster.setLastModifiedOn(new Timestamp(new Date().getTime()));
	    String updates = updateDiffFinder.getUpdatedDiff(oldDBMappingMaster.get(), dBMappingMaster);
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "DM Mapping Master has been updated successfully: updated data,"+updates + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
	    return dBMappingMasterRepository.save(dBMappingMaster);
	} catch (RuntimeException e) {
	 //   log.error("Error while updating DB Mapping Master: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}
    }

    @Override
    public void deleteDbMappingMasterById(Long id, Integer mvnoId) {
	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
	try {

	    Optional<DBMappingMaster> dbMappingMasterOptional = validateDBMappingMasterById(id, mvnoId, true);
	    if (!dbMappingMasterOptional.isPresent())
		throw new IllegalArgumentException(
			"No record found for  DB Mapping Master with  DB Mapping Master id : '" + id
				+ "' Or you do not have access to update/delete this record");

	    if (radiusProfileRepository.countByMappingMasterMappingMasterId(id) == 0) {
		dbMappingService.deleteByMappingMasterId(id, mvnoId);
		dBMappingMasterRepository.deleteById(id);
		//log.info("DB Mapping Master deleted successfully: " + id);
	    } else {
		throw new RuntimeException(
			"This operation will not allow as this DB Mapping Master is used for Radius Profile creation.");
	    }
	} catch (RuntimeException e) {
	    log.error("Error while deleting DB Mapping Master: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}

    }

    private Optional<DBMappingMaster> validateDBMappingMasterById(Long id, Integer mvnoId, Boolean isUpdateOrDelete) {

	try {

	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
		throw new IllegalArgumentException("Please enter valid DB Mapping Master id.");
	    }

	    QDBMappingMaster qdbMappingMaster = QDBMappingMaster.dBMappingMaster;
	    BooleanExpression boolExp = qdbMappingMaster.isNotNull();
	    if ((mvnoId == null || mvnoId != 1) && !isUpdateOrDelete)
		boolExp = boolExp.and(qdbMappingMaster.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		if((mvnoId == null || mvnoId != 1) && isUpdateOrDelete)
			boolExp = boolExp.and(qdbMappingMaster.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qdbMappingMaster.mappingMasterId.eq(id));
	    Optional<DBMappingMaster> dbMappingMaster = dBMappingMasterRepository.findOne(boolExp);

	    return dbMappingMaster;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateDBMappingMasterData(DBMappingMaster dbMappingMaster, boolean isUpdate) {

	if (!ValidateCrudTransactionData.validateStringTypeFieldValue(dbMappingMaster.getName())) {
	    throw new IllegalArgumentException(
		    "DB Mapping Master name is mandatory. Please enter valid Mapping Master name.");
	} else if (!dbMappingMaster.getStatus().equalsIgnoreCase(RadiusConstants.ACTIVE)
		&& !dbMappingMaster.getStatus().equalsIgnoreCase(RadiusConstants.IN_ACTIVE)) {
	    throw new IllegalArgumentException("Please enter valid DB Mapping Master status. It should be '"
		    + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
	} else if (!isUpdate) {
	    if (!findForUpdateAndDelete(dbMappingMaster, false).isEmpty())
		throw new IllegalArgumentException("DB Mapping Master already exist with name '"
			+ dbMappingMaster.getName() + "'. Please enter unique name.");
	} else if (isUpdate) {
	    List<DBMappingMaster> list = findForUpdateAndDelete(dbMappingMaster, true);
	    if (list.size() > 1) {
		throw new IllegalArgumentException("DB Mapping Master already exist with name '"
			+ dbMappingMaster.getName() + "'. Please enter unique name.");
	    } else if (list.size() == 0)
		throw new IllegalArgumentException("No record found with DB maping master name '"
			+ dbMappingMaster.getName() + "' OR DB Mapping Master name cannot be changed");
	}
    }

    @Override
    public String changeStatus(Long dbMappingMasterId, String status, Integer mvnoId ,HttpServletRequest request) {
	try {
	    Optional<DBMappingMaster> dBMappingMaster = validateDBMappingMasterById(dbMappingMasterId, mvnoId, true);
	    if (!dBMappingMaster.isPresent())
		throw new IllegalArgumentException(
			"No record found for  DB Mapping Master with  DB Mapping Master id : '" + dbMappingMasterId
				+ "' Or you do not have access to update/delete this record");

	    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
		throw new RuntimeException("DB Mapping Master status is mandatory. Please enter valid status.");
	    } else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) {
		throw new IllegalArgumentException("Please enter valid DB Mapping Master status. It should be '"
			+ RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
	    }
		String oldstatus=dBMappingMaster.get().getStatus();
	    dBMappingMaster.get().setStatus(status);
	    DBMappingMaster mappingMaster = dBMappingMasterRepository.save(dBMappingMaster.get());
	    String msg = "";
	    if (status.equals(RadiusConstants.ACTIVE)) {
		msg = "DB Mapping Master '" + mappingMaster.getMappingMasterId() + "' has been activated successfully.";
	    } else {
		msg = "DB Mapping Master '" + mappingMaster.getMappingMasterId()
			+ "' has been inactivated successfully.";
	    }
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "DB Mapping's status has been updated successfully.:,"+oldstatus+"updated to"+status   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return msg;
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<DBMappingMaster> getDBMappingMasters(Integer mvnoId) {
	try {
	    QDBMappingMaster qDBMappingMaster = QDBMappingMaster.dBMappingMaster;
	    BooleanExpression exp = qDBMappingMaster.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		exp = exp.and(qDBMappingMaster.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
	    exp = exp.and(qDBMappingMaster.status.eq(RadiusConstants.ACTIVE));
	    return (List<DBMappingMaster>) dBMappingMasterRepository.findAll(exp);
	} catch (Throwable e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

}
