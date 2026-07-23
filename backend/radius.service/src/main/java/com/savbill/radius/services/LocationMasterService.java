package com.savbill.radius.services;

import com.savbill.radius.entity.LocationMaster;
import com.savbill.radius.kafka.message.LocationMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface LocationMasterService {
	LocationMaster saveLocationMaster(LocationMaster locationmaster, Integer mvnoId);

	List<LocationMaster> findAllLocationMaster(Integer mvnoId);

	LocationMaster findlocationMasterById(Long locationMasterId, Integer mvnoId);

	LocationMaster updateLocation(LocationMaster locationMaster, Integer mvnoId, HttpServletRequest request);

	void deleteLocationById(Long locationMasterId, Integer mvnoId);

	List<LocationMaster> findLocation(String name, Integer mvnoId);

	String updateLocationStatus(String name, String status, Integer mvnoId,HttpServletRequest request);

	void addWifiLocation(LocationMessage locationMaster,Boolean isUpdate,Boolean isDelete,HttpServletRequest request);
}
