package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.helper.RadiusProfileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface RadiusProfileService {
	
	List<RadiusProfile> searchByName(String name, Integer mvnoId);
	RadiusProfile findByName(String name, Integer mvnoId);
	RadiusProfile findById(Long id, Integer mvnoId);
	List<RadiusProfile> findByProxyServerId(Long proxyServerId, Integer mvnoId);
	List<RadiusProfile> findAll(Integer mvnoId);
	void deleteById(Long id, Integer mvnoId);
	RadiusProfile save(RadiusProfileDto radiusProfile, Integer mvnoId, MultipartFile[] trustStoreFile, MultipartFile[] keyStoreFile);

	RadiusProfile update(RadiusProfileDto radiusProfile, Integer mvnoId, HttpServletRequest request, MultipartFile[] trustStoreFile, MultipartFile[] keyStoreFile);
	String changeRadiusProfileStatus(String name, String status, Integer mvnoId,HttpServletRequest request);
}
