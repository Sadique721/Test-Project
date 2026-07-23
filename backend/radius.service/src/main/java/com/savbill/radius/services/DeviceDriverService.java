package com.savbill.radius.services;

import java.util.List;

import javax.naming.NamingException;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.entity.DeviceDriver;
import com.savbill.radius.helper.DeviceDriverDTO;

public interface DeviceDriverService
{
	List<DeviceDriver> findAll(Integer mvnoId);
	DeviceDriver findById(Long id, Integer mvnoId);
	DeviceDriver add(DeviceDriverDTO deviceDriverDto, Integer mvnoId);
	DeviceDriver update(DeviceDriverDTO deviceDriverDto, Integer mvnoId);
	void delete(Long deviceDriverId, Integer mvnoId);

	Boolean authUser(String username , String password , String address);

	Boolean verifyAuthUser(String name,Integer mvnoId);

	List<DeviceDriver> getDeviceDriverByName(String deviceDriverName , Integer mvnoId);


	Boolean validateByName(String name , Integer mvnoId);

	CustomerData isUserExist(String configurationName , String username , String password , Integer mvnoId) throws NamingException;


}
