package com.savbill.radius.services.impl;

import java.net.InetSocketAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusServer;
import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.services.CacheConfigService;

@Service
public class TestCacheMechanism extends RadiusServer
{
	@Autowired
	CacheConfigService cacheConfigService;
	
	public List<RadiusProfile> findRadiusProfileCache()
	{
		//		System.out.println("Calling from another method---------Start");
		List<RadiusProfile> cacheAuthProfileData = cacheConfigService.cacheAuthProfileData();
		//		System.out.println("Calling from another method---------End");
		return cacheAuthProfileData;
	}

	@Override
	public String getSharedSecret(InetSocketAddress client) {
		
		return null;
	}

	@Override
	public String getUserPassword(String userName) {
		
		return null;
	}
	
	@Override
	public RadiusPacket accountingRequestReceived(AccountingRequest request, InetSocketAddress client) 
	{
		return request;
		
	}
	
	@Override
	public RadiusPacket accessRequestReceived(AccessRequest request,InetSocketAddress client) {
		return request;
		
	}
}
