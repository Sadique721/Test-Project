package com.savbill.radius.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.services.impl.TestCacheMechanism;
import com.savbill.radius.utils.RadiusConstants;

@RestController
@RequestMapping("/SavbillRadius")
public class CacheConfigController 
{
	@Autowired
	APIResponseController apiResponseController;
	@Autowired
	CacheConfigService cacheConfigService;
	@Autowired
	TestCacheMechanism testCacheMechanism;
	
	@GetMapping("/configServer")
    public ResponseEntity<Map<String, Object>> findAllConfigServerData()
    {
		Map<String, Object> response = new HashMap<>();
		try 
		{
			response = cacheConfigService.cacheServerConfig();
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		} 
		catch (Exception e) 
		{
		    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
		    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
    }
    
    @GetMapping("/radiusProfile")
    public ResponseEntity<Map<String, Object>> findAllRadiusProfileData()
    {
		Map<String, Object> response = new HashMap<>();
		try 
		{
			List<RadiusProfile> cacheAuthProfileData = cacheConfigService.cacheAuthProfileData();
			response.put("radiusProfileList", cacheAuthProfileData);
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		} 
		catch (Exception e) 
		{
		    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
		    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
    }
    
    @GetMapping("/radiusProfileAgain")
    public ResponseEntity<Map<String, Object>> findAllRadiusProfileDataAgain()
    {
		Map<String, Object> response = new HashMap<>();
		try 
		{
			List<RadiusProfile> radiusProfileData = testCacheMechanism.findRadiusProfileCache();
			response.put("radiusProfileList", radiusProfileData);
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		} 
		catch (Exception e) 
		{
		    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
		    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
    }
    
    @GetMapping("/dbMapping")
    public ResponseEntity<Map<String, Object>> findAllDbMappingData()
    {
		Map<String, Object> response = new HashMap<>();
		try 
		{
			List<DBMapping> cacheDbMappingData = cacheConfigService.cacheDbMappingData();
			response.put("dbMappingList", cacheDbMappingData);
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		} 
		catch (Exception e) 
		{
		    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
		    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
    }

	@GetMapping("/reloadCache")
	public ResponseEntity<Map<String, Object>> reloadCache()
	{
		Map<String, Object> response = new HashMap<>();
		try
		{
			cacheConfigService.reloadCache();;
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		}
		catch (Exception e)
		{
			response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
			return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
	}


	@GetMapping("/reloadCacheByCacheName")
	public ResponseEntity<Map<String, Object>> reloadCacheByCacheName(@RequestParam(name = "cacheName", required = true) String cacheName)
	{
		Map<String, Object> response = new HashMap<>();
		try
		{
			cacheConfigService.reloadCache(cacheName);;
			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
		}
		catch (Exception e)
		{
			response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
			return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
	}
}
