package com.savbill.radius.services;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;

import java.util.List;
import java.util.Map;

public interface CacheConfigService {
    public Map<String, Object> cacheServerConfig();

    public List<RadiusProfile> cacheAuthProfileData();

    public List<RadiusProfile> cacheAcctProfileData();

    public List<DBMapping> cacheDbMappingData();

    public List<CoaDMProfile> cacheCoADMProfileData();

    public Map<String, Object> cacheClientConfig();
    public Map<String, Object> cacheClientConfigOnRunTime();

    public Map<String, Object> cacheProxyData();

    public List<RadiusProfile> cachedynaAuthProfileData();

    List<VLANManagement> cacheVlanManagement();

    public Map<Long, List<String>> cachePermission();

    public List<ConfigurationService> cacheSystemConfiguration();

    public Map<String, FaultyMAC> cacheFaultyMacData();
    public Map<String, FaultyMAC> cacheFaultyMacDataAtRunTime();

    public void reloadCache();

    public void reloadCache(String cacheName);

    Map<String,List<VLANManagement>> cacheVlanManagement1();
}

