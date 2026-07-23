package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.helper.DBMappingDto;

import javax.servlet.http.HttpServletRequest;

public interface DBMappingService {

    DBMapping saveDBMapping(DBMappingDto dbMappingDto, Integer mvnoId);

	List<DBMapping> findAllDbMappings(Integer mvnoId);

	List<DBMapping> findDBMappingByDBMappingMasterId(Long dbMappingMasterId, Integer mvnoId);

	List<DBMapping> updateDBMapping(List<DBMapping> dbMappingList, Long mappingMasterId, Integer mvnoId, HttpServletRequest request);

	void deleteDBMappingByMappingId(Long dbMappingId, Integer mvnoId);
	void deleteByMappingMasterId(Long dbMappingMasterId, Integer mvnoId);
}
