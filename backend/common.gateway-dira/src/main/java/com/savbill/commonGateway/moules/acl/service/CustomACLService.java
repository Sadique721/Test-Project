package com.savbill.commonGateway.moules.acl.service;

import com.savbill.commonGateway.common.CommonUtils;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.acl.repository.ACLRepositroy;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.Map;

@Service
public class CustomACLService  {

    private static final Logger logger = LoggerFactory.getLogger(CustomACLService.class);

    @Autowired
    private ACLRepositroy repository;

    private CacheManager cacheManager;

    public static ConcurrentHashMap<Long, List<String>> permissionsMap = new ConcurrentHashMap<>();

    public CustomACLService() {
        ApplicationLogger.logger.info("Constructor called");
        permissionsMap = new ConcurrentHashMap<>();
//        initCache();
//        ApplicationLogger.logger.info("Constructor called");
//        cacheManager = CacheManager.getInstance();
//        repository = new ACLRepositroy();
//        initCache();
    }

	/*private  Map<String,Map<String,Integer>>  aclMap = null;

	public synchronized Map<String,Map<String,Integer>>  getACLMap(){
		if(aclMap == null){
			aclMap=repository.generateACLMap();
		}
		return aclMap;
	}*/

//    @Autowired
//    public void initCache() {
//        String METHOD = "initCache()";
//        Cache permissionsCache = null;
//
//        try {
//            ApplicationLogger.logger.info(METHOD + " Started");
//
//            Object[] resultFields = null;
//            List<String> operationList = null;
//            String operation = "";
//            Element element = null;
//            permissionsCache = cacheManager.getCache("permissionsCache");
//            List<Object[]> resultSet = repository.fetchPermissions();
//
//            if (resultSet != null && resultSet.size() > 0) {
//                for (int i = 0; i < resultSet.size(); i++) {
//                    operationList = null;
//                    Long roleId = 0L;
//
//                    resultFields = resultSet.get(i);
//                    if(resultFields[0] == null)
//                        continue;
//                    roleId = Long.valueOf(resultFields[0].toString());
//                    operation = resultFields[1].toString();
//
////                    ApplicationLogger.logger.info("Role: " + roleId + " Operation: " + operation);
//
//                    element = permissionsCache.get(roleId);
//                    if (element != null) {
//                        operationList = (List<String>) element.getObjectValue();
//                    }
//                    if (operationList == null) {
//                        operationList = new ArrayList<>();
//                    }
//                    operationList.add(operation);
//
//                    permissionsCache.put(new Element(roleId, operationList));
//                }
//            }
//
//            ApplicationLogger.logger.info(METHOD + " Ended");
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (permissionsCache != null) {
//                permissionsCache.flush();
//            }
//        }
//    }

    @Autowired
    public void initCache() {
        String METHOD = "initCache()";
        try {
            ApplicationLogger.logger.info(METHOD + " Started");

            List<Object[]> resultSet = repository.fetchPermissions();
            List<String> operationList = null;
            String operation =null;
                    Long roleId = null;
            if (resultSet != null && !resultSet.isEmpty()) {
                for (Object[] resultFields : resultSet) {
                    BigInteger roleIdBigInteger = (BigInteger) resultFields[0];
                    roleId = roleIdBigInteger.longValue(); // Convert BigInteger to Long
                    operation = (String) resultFields[1];
                    if (!permissionsMap.containsKey(roleId)) {
                        permissionsMap.put(roleId, new ArrayList<>()); // Create a new list if roleId doesn't exist
                    }
                    permissionsMap.get(roleId).add(operation);
                }

            }

            ApplicationLogger.logger.info(METHOD + " Ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCache(Long roleId) {
        String METHOD = "UpdateCache()";
        try {
            ApplicationLogger.logger.info(METHOD + " Started");

            List<String> resultSet = repository.fetchPermissions(roleId);

            if (resultSet != null) {
                permissionsMap.put(roleId, resultSet);
            } else {
                // Handle the case when the result set is null
                ApplicationLogger.logger.warn("Result set from repository.fetchPermissions(roleId) is null for roleId: " + roleId);
            }

            ApplicationLogger.logger.info(METHOD + " Ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadCache() {

//        Cache aclCache = null;
//        Cache domainsCache = null;
//        Cache operationsCache = null;
//        Cache permissionsCache = null;
        try {
//           initCache();

            // Optionally, reset other cached objects or resources
            CommonUtils.resetCachedObjects();

            ApplicationLogger.logger.info("Cache reloaded successfully");
//            aclCache = cacheManager.getCache("aclCache");
//            domainsCache = cacheManager.getCache("domainsCache");
//            operationsCache = cacheManager.getCache("operationsCache");
//            permissionsCache = cacheManager.getCache("permissionsCache");
//
//            if (aclCache != null) {
//                aclCache.flush();
//            }
//            if (domainsCache != null) {
//                domainsCache.flush();
//            }
//            if (operationsCache != null) {
//                operationsCache.flush();
//            }
//            if (permissionsCache != null) {
//                permissionsCache.flush();
//            }
//            initCache();
//            CommonUtils.resetCachedObjects();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error to reload cache: "+e.getMessage());
        }
    }
}
