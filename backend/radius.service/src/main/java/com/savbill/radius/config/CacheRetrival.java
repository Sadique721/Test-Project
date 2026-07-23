package com.savbill.radius.config;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.services.impl.CacheConfigServiceImpl;
import com.savbill.radius.spring.SpringContext;
import com.savbill.radius.utils.RadiusConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CacheRetrival {

    public List<RadiusProfile> getAuthProfileData() {
        try {
            List<RadiusProfile> cacheAuthProfileData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheAuthProfileData();
            cacheAuthProfileData = cacheAuthProfileData.stream().filter(radiusProfile -> RadiusConstants.ACTIVE.equalsIgnoreCase(radiusProfile.getStatus())).collect(Collectors.toList());
            return cacheAuthProfileData;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<RadiusProfile> getDynaAuthProfileData() {
        try {
            List<RadiusProfile> cachedynaAuthProfileData = SpringContext.getBean(CacheConfigServiceImpl.class).cachedynaAuthProfileData();
            return cachedynaAuthProfileData;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<CoaDMProfile> getCoADMProfileData() {
        try {
            List<CoaDMProfile> cacheAuthProfileData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheCoADMProfileData();
            return cacheAuthProfileData;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> cacheClientConfig() {
        try {
            Map<String, Object> cacheClientConfigData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheClientConfig();
            return cacheClientConfigData;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<RadiusProfile> getAcctProfileData() {
        try {
            List<RadiusProfile> cacheAcctProfileData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheAcctProfileData();
//            cacheAcctProfileData = cacheAcctProfileData.stream().filter(radiusProfile -> RadiusConstants.ACTIVE.equalsIgnoreCase(radiusProfile.getStatus())).collect(Collectors.toList());
            return cacheAcctProfileData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<VLANManagement> getVlanDetailsData() {
        try {
            return SpringContext.getBean(CacheConfigServiceImpl.class).cacheVlanManagement();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, List<VLANManagement>> getVlanDetailsData1() {
        try {
            return SpringContext.getBean(CacheConfigServiceImpl.class).cacheVlanManagement1();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> getConfigServerData() {
        try {
            Map<String, Object> cacheServerConfigData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheServerConfig();
            return cacheServerConfigData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> getProxyServerData() {
        try {
            Map<String, Object> cacheProxyData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheProxyData();
            return cacheProxyData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<DBMapping> getDbMappingData() {
        try {
            List<DBMapping> cacheDbMappingData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheDbMappingData();
            return cacheDbMappingData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Map<Long, List<String>> getPermissionServerData() {
        try {
            Map<Long, List<String>> cacheProxyData = SpringContext.getBean(CacheConfigServiceImpl.class).cachePermission();
            return cacheProxyData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<ConfigurationService> getConfiguraitonData(Integer mvnoId, String name) {
        try {
            List<ConfigurationService> cacheProxyData = SpringContext.getBean(CacheConfigServiceImpl.class).cacheSystemConfiguration();
            return cacheProxyData.stream().filter(configurationService -> configurationService.getName().equalsIgnoreCase(name) && configurationService.getMvnoId().equals(mvnoId)).findFirst();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<String> getFaultyMacData() {
        try {
            Map<String, FaultyMAC> faultyMACS = SpringContext.getBean(CacheConfigServiceImpl.class).cacheFaultyMacData();
            return new ArrayList<>(faultyMACS.keySet());
            //.stream().filter(faultyMAC -> (faultyMAC.getMackId() != null && !faultyMAC.getIsDeleted())).map(FaultyMAC::getMackId).collect(Collectors.toList());
        } catch (Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    public Map<String, FaultyMAC> getFaultyMacList() {
        try {
            Map<String, FaultyMAC> faultyMACS = SpringContext.getBean(CacheConfigServiceImpl.class).cacheFaultyMacData();
            return faultyMACS;//faultyMACS.stream().filter(faultyMAC -> (faultyMAC.getMackId() != null && !faultyMAC.getIsDeleted())).collect(Collectors.toList());
        } catch (Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }
}
