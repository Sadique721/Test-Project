package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.DBMappingMaster;
import com.savbill.radius.helper.DBMappingMasterDto;

import javax.servlet.http.HttpServletRequest;

public interface DBMappingMasterService {

	List<DBMappingMaster> findAllDBMappingMasters(Integer mvnoId);

	DBMappingMaster findDBMappingMasterById(Long dbMapingMastersId, Integer mvnoId);

	DBMappingMaster updateDBMappingMaster(DBMappingMaster dBMappingMaster, Integer mvnoId, HttpServletRequest request);

	void deleteDbMappingMasterById(Long dbMapingMastersId, Integer mvnoId);

	DBMappingMaster saveDbMappingMaster(DBMappingMasterDto dBMappingMasterDto, Integer mvnoId);

	List<DBMappingMaster> findDBMappingMastersByName(String name, Integer mvnoId);

	String changeStatus(Long dbMappingMasterId, String status, Integer mvnoId,HttpServletRequest request);

	List<DBMappingMaster> getDBMappingMasters(Integer mvnoId);

}
