package com.savbill.radius.utils;

import java.util.List;
import java.util.Map;

import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.services.impl.CacheConfigServiceImpl;
import com.savbill.radius.spring.SpringContext;

//@Component
public class FillupCacheOnStartup 
{
	public FillupCacheOnStartup() 
	{
		CacheConfigServiceImpl bean = SpringContext.getApplicationContext().getBean(CacheConfigServiceImpl.class);
		
		if(bean != null)
		{
			Map<String, Object> cacheServerConfig = bean.cacheServerConfig();
			//		System.out.println("Data :" + cacheServerConfig);
			
			List<RadiusProfile> cacheAuthProfileData = bean.cacheAuthProfileData();
			//		System.out.println("Data :" + cacheAuthProfileData);
			
			List<DBMapping> cacheDbMappingData = bean.cacheDbMappingData();
			//		System.out.println("Data :" + cacheDbMappingData);
			
			List<RadiusProfile> cacheAcctProfileData = bean.cacheAcctProfileData();
			//		System.out.println("Data :" + cacheAcctProfileData);
			
			List<RadiusProfile> cachedynaAuthProfileData=bean.cachedynaAuthProfileData(); 
			//		System.out.println("Data :" + cachedynaAuthProfileData);

		}
	}
	
}
