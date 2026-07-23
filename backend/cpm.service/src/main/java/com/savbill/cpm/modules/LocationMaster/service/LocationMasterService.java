package com.savbill.cpm.modules.LocationMaster.service;


import com.savbill.cpm.modules.LocationMaster.domain.LocationMaster;
import com.savbill.cpm.modules.LocationMaster.domain.LocationMasterMapping;
import com.savbill.cpm.modules.LocationMaster.module.LocationMasterDto;
import com.savbill.cpm.modules.LocationMaster.module.LocationMasterMappingDto;
import com.savbill.cpm.modules.LocationMaster.module.UpdateLocationMasterDto;
import com.savbill.cpm.modules.Reseller.mapper.PageableResponse;
import com.savbill.cpm.modules.Voucher.module.PaginationDTO;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationMasterService {
	LocationMaster saveLocationMaster(LocationMasterDto locationmasterDto, Long mvnoId);

	PageableResponse<LocationMaster> findAllLocationMaster(Long mvnoId, String name, PaginationDTO paginationDTO);

	LocationMaster findlocationMasterById(Long locationMasterId, Long mvnoId);

	LocationMaster updateLocation(UpdateLocationMasterDto locationDto, Long mvnoId);

	void deleteLocationById(Long locationMasterId, Long mvnoId);

	List<LocationMaster> findLocation(String name, Long mvnoId);

	String updateLocationStatus(String name, String status, Long mvnoId);

	List<LocationMasterMappingDto> getAllMacFromLocations(@Param("locationIds") List<Long> locationIds, Boolean isParentLocation);

	List<LocationMaster> getLocationFromMac(@Param("mac") String mac);

	List<LocationMasterMapping> saveLocationMasterMapping(List<LocationMasterMappingDto> locationMasterMappingDtos, LocationMaster locationMaster);

	//List<LocationMaster> findLocationByPlan(Long planId, Long mvnoId);
}
