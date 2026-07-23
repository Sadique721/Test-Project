package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.eap.util.KeyStoreImpl;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class CacheConfigServiceImpl implements CacheConfigService {
    private static final Logger log = LoggerFactory.getLogger(CacheConfigServiceImpl.class);

    @Autowired
    private RadiusProfileRepository radiusProfileRepository;

    @Autowired
    private DBMappingRepository dbMappingRepository;

    @Autowired
    private CoaDMProfileRepository coaDMProfileRepository;

    @Autowired
    private CoaDMProfileAttributeRepository coaDMProfileAttributeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientGroupRepository clientGroupRepository;

    @Autowired
    private ClientReplyRepository clientReplyRepository;

    @Autowired
    private ProxyServerRepository proxyServerRepository;

    @Autowired
    private ACLRepositroy repository;

    @Autowired
    private ConfigurationServiceRepository configuraionRepository;

    @Autowired
    private AccessResponseRepo accessResponseRepo;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FaultyMACKRepocitory faultyMACKRepocitory;

    @Autowired
    private VlanManagementRepository vlanManagementRepository;

    Map<String, Object> proxyMap = null;

    Map<String, Object> clientMap = null;

    List<CoaDMProfile> coaDMProfileListReturn = null;

    List<VLANManagement> vlanManagements = null;

    Map<String,List<VLANManagement>> vlanManagements1 = null;

    List<RadiusProfile> radiusProfileList = null;

    List<RadiusProfile> radiusProfileListAcct = null;

    List<RadiusProfile> radiusProfileListDyna = null;

    List<DBMapping> dbMappingList = null;

    //    List<FaultyMAC> faultyMacList = null;
    Map<String, FaultyMAC> faultyMacList = null;

    Map<Long, List<String>> cacheList = null;
    @Autowired
    private DictionaryAttributeRepository dictionaryAttributeRepository;

    @Override
    //@Cacheable(cacheNames = AAAConstant.PROXYCACHE)
    public Map<String, Object> cacheProxyData() {
        try {
            if (proxyMap == null) {
                proxyMap = new HashMap<>();
                List<ProxyServer> proxyServerList = new ArrayList<ProxyServer>();
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                proxyServerList = proxyServerRepository.findAll();
                Iterator<ProxyServer> iter = proxyServerList.iterator();
                while (iter.hasNext()) {
                    ProxyServer proxyServerdata = iter.next();
                    proxyMap.put(proxyServerdata.getId().toString(), proxyServerdata);
                }
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return proxyMap;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.CLIENTDETAILCACHE)
    public Map<String, Object> cacheClientConfig() {
//		log.info("First time call method implementation.");
        try {
            if (clientMap == null) {
                clientMap = new HashMap<>();
                //		System.out.println("First time call method implementation to fill up the cache cacheClientConfig --------Start");
                List<Client> clientList = clientRepository.findAll();
                for (int i = 0; i < clientList.size(); i++) {
                    Client clientData = clientList.get(i);
                    Long clientGroupId = clientData.getClientGroupId();

                    if (clientGroupId == null || clientGroupId == 0L) {
                        List<ClientGroupMapping> clientGroupMappings = clientData.getClientGroupMappings();
//						clientGroupMappings = clientGroupMappings.stream().sorted(Comparator.comparing(ClientGroupMapping::getPriority).reversed()).collect(Collectors.toList());
                        clientGroupMappings = clientGroupMappings.stream()
                                .sorted(Comparator.comparing(clientGroupMapping -> {
                                    Integer priority = null;
                                    try {
                                        priority = clientGroupMapping.getPriority(); // Ensure this method exists and returns Integer
                                    } catch (Exception e) {
                                        // handle the error, if getPriority is missing
                                        priority = 0;
                                    }
                                    return priority != null ? priority : 0;
                                }, Comparator.reverseOrder()))
                                .collect(Collectors.toList());

                        setClientGroupMapping(clientGroupMappings);
                    } else {
                        Optional<ClientGroup> clientGroup = clientGroupRepository.findById(clientGroupId);
                        if (clientGroup.isPresent()) {
                            List<ClientReply> clientReply = clientReplyRepository.findbyClientGroup(clientGroupId);
                            try {
                                ClientGroup clientGroupData = clientGroup.get();
                                if (!CollectionUtils.isEmpty(clientReply) && "Enable".equalsIgnoreCase(clientGroupData.getAuthenticationProfile())) {
                                    List<ClientReply> acceptClientReply = clientReply.stream().filter(clientRep -> !clientRep.isRejectAttribute()).collect(Collectors.toList());
                                    clientGroupData.setClientReplyList(acceptClientReply);
                                    List<ClientReply> rejectClientReply = clientReply.stream().filter(ClientReply::isRejectAttribute).collect(Collectors.toList());
                                    clientGroupData.setClientReplyListForRejectAuthResponse(rejectClientReply);

                                } else {
                                    clientGroupData.setClientReplyList(clientReply);
                                }
                                //Set VendorId and Input/output type
                                String inputType = clientGroupData.getInputPacketAttributeValue();
                                String outputType = clientGroupData.getOutputPacketAttributeValue();
                                Optional<DictionaryAttribute> inputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(inputType);
                                inputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                                    clientGroupData.setInPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                                    clientGroupData.setInPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                                });
                                Optional<DictionaryAttribute> outputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(outputType);
                                outputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                                    clientGroupData.setOutPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                                    clientGroupData.setOutPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                                });
                                //Set Access Response messages
                                if (clientGroupData.getAuthenticationProfile() != null && clientGroupData.getAuthenticationProfile().equalsIgnoreCase("Enable")) {
                                    List<AccessResponse> accessResponses = accessResponseRepo.findAllByIsDeleteFalse();
                                    clientGroupData.setAccessResponses(accessResponses);
                                }
                                clientData.setClientGroupData(clientGroupData);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                log.error("Exception at get Client group cache: " + ex.getMessage());
                            }
                        }
                    }
                    clientMap.put(clientData.getClientIpAddress(), clientData);
                }
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return clientMap;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while configuring cache" + e.getMessage());
        }
        return clientMap;
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.CLIENTDETAILCACHE)
    public Map<String, Object> cacheClientConfigOnRunTime() {
//		log.info("First time call method implementation.");
        try {
//            if (clientMap == null) {
                Map<String, Object> tempObj = new HashMap<>();
                //		System.out.println("First time call method implementation to fill up the cache cacheClientConfig --------Start");
                List<Client> clientList = clientRepository.findAll();
                for (int i = 0; i < clientList.size(); i++) {
                    Client clientData = clientList.get(i);
                    Long clientGroupId = clientData.getClientGroupId();

                    if (clientGroupId == null || clientGroupId == 0L) {
                        List<ClientGroupMapping> clientGroupMappings = clientData.getClientGroupMappings();
//						clientGroupMappings = clientGroupMappings.stream().sorted(Comparator.comparing(ClientGroupMapping::getPriority).reversed()).collect(Collectors.toList());
                        clientGroupMappings = clientGroupMappings.stream()
                                .sorted(Comparator.comparing(clientGroupMapping -> {
                                    Integer priority = null;
                                    try {
                                        priority = clientGroupMapping.getPriority(); // Ensure this method exists and returns Integer
                                    } catch (Exception e) {
                                        // handle the error, if getPriority is missing
                                        priority = 0;
                                    }
                                    return priority != null ? priority : 0;
                                }, Comparator.reverseOrder()))
                                .collect(Collectors.toList());

                        setClientGroupMapping(clientGroupMappings);
                    } else {
                        Optional<ClientGroup> clientGroup = clientGroupRepository.findById(clientGroupId);
                        if (clientGroup.isPresent()) {
                            List<ClientReply> clientReply = clientReplyRepository.findbyClientGroup(clientGroupId);
                            try {
                                ClientGroup clientGroupData = clientGroup.get();
                                if (!CollectionUtils.isEmpty(clientReply) && "Enable".equalsIgnoreCase(clientGroupData.getAuthenticationProfile())) {
                                    List<ClientReply> acceptClientReply = clientReply.stream().filter(clientRep -> !clientRep.isRejectAttribute()).collect(Collectors.toList());
                                    clientGroupData.setClientReplyList(acceptClientReply);
                                    List<ClientReply> rejectClientReply = clientReply.stream().filter(ClientReply::isRejectAttribute).collect(Collectors.toList());
                                    clientGroupData.setClientReplyListForRejectAuthResponse(rejectClientReply);

                                } else {
                                    clientGroupData.setClientReplyList(clientReply);
                                }
                                //Set VendorId and Input/output type
                                String inputType = clientGroupData.getInputPacketAttributeValue();
                                String outputType = clientGroupData.getOutputPacketAttributeValue();
                                Optional<DictionaryAttribute> inputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(inputType);
                                inputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                                    clientGroupData.setInPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                                    clientGroupData.setInPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                                });
                                Optional<DictionaryAttribute> outputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(outputType);
                                outputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                                    clientGroupData.setOutPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                                    clientGroupData.setOutPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                                });
                                //Set Access Response messages
                                if (clientGroupData.getAuthenticationProfile() != null && clientGroupData.getAuthenticationProfile().equalsIgnoreCase("Enable")) {
                                    List<AccessResponse> accessResponses = accessResponseRepo.findAllByIsDeleteFalse();
                                    clientGroupData.setAccessResponses(accessResponses);
                                }
                                clientData.setClientGroupData(clientGroupData);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                log.error("Exception at get Client group cache: " + ex.getMessage());
                            }
                        }
                    }
                    tempObj.put(clientData.getClientIpAddress(), clientData);
                }
                //		System.out.println("First time call method implementation to fill up the cache--------End");
//            }
            clientMap = tempObj;
            return clientMap;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while configuring cache" + e.getMessage());
        }
        return clientMap;
    }

    @Override
    ////@Cacheable(cacheNames = AAAConstant.SERVERCACHING_CACHE)
    public Map<String, Object> cacheServerConfig() {
//		log.info("First time call method implementation.");
        Map<String, Object> serverConfigMap = new HashMap<>();
        try {
            Properties prop = new Properties();
            serverConfigMap.put(AAAConstant.AUTHPORT, "1812");
            serverConfigMap.put(AAAConstant.ACCTPORT, "1813");
            serverConfigMap.put(AAAConstant.SERVERIP, "0.0.0.0");
            serverConfigMap.put(AAAConstant.WEBSERVICEIP, "0.0.0.0");
            serverConfigMap.put(AAAConstant.WEBSERVICEPORT, "9090");
        } catch (Exception e) {
            log.error("Error while configuring cache", e);
        }
        return serverConfigMap;
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.COADMPROFILECONFIGCACHE)
    public List<CoaDMProfile> cacheCoADMProfileData() {
        try {
            if (coaDMProfileListReturn == null) {
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                List<CoaDMProfile> coaDMProfileList = coaDMProfileRepository.findAll();
                coaDMProfileListReturn = new ArrayList<CoaDMProfile>();
                for (int i = 0; i < coaDMProfileList.size(); i++) {
                    List<CoaDMProfileAttribute> CoaDMProfileAttributeList = coaDMProfileAttributeRepository.findCoaDMProfileAttributeByCoaDMProfileId(coaDMProfileList.get(i).getCoaDMProfileId());
                    CoaDMProfile coaDMProfile = coaDMProfileList.get(i);
                    coaDMProfile.setCoaDMProfileAttributeList(CoaDMProfileAttributeList);
                    coaDMProfileListReturn.add(coaDMProfile);
                }
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return coaDMProfileListReturn;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.AUTHPROFILECONFIG_CACHE)
    public List<RadiusProfile> cacheAuthProfileData() {
        try {
            //		System.out.println("First time call method implementation to fill up the cache--------Start");
            if (radiusProfileList == null) {
                radiusProfileList = radiusProfileRepository.findByRequestTypeOrderByPriorityDesc("Authentication");
            }

            initializeKeyStore(radiusProfileList);
            //		System.out.println("First time call method implementation to fill up the cache--------End");
            return radiusProfileList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.DYNAAUTHPROFILECONFIG_CACHE)
    public List<RadiusProfile> cachedynaAuthProfileData() {
        try {
            if (radiusProfileListDyna == null) {
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                radiusProfileListDyna = radiusProfileRepository.findByRequestTypeOrderByPriorityDesc("DynaAuth");
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return radiusProfileListDyna;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.DBMAPPING_CACHE)
    public List<DBMapping> cacheDbMappingData() {
        try {
            if (dbMappingList == null) {
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                dbMappingList = dbMappingRepository.findAll();
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return dbMappingList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.FAULTY_MAC_CACHE)
    public Map<String, FaultyMAC> cacheFaultyMacData() {
        try {
            if (faultyMacList == null) {
                // Initialize the cache as a HashMap
                faultyMacList = faultyMACKRepocitory.findAllByIsDeletedFalse()
                        .stream()
                        .peek(faultyMAC ->
                                faultyMAC.setMackId(faultyMAC.getMackId().toLowerCase().replaceAll("[-:.]", ""))
                        )
                        .collect(Collectors.toMap(FaultyMAC::getMackId, faultyMAC -> faultyMAC));
            }
            return faultyMacList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.FAULTY_MAC_CACHE)
    public Map<String, FaultyMAC> cacheFaultyMacDataAtRunTime() {
        try {
                // Initialize the cache as a HashMap
            Map<String, FaultyMAC> tempObj = new HashMap<>();
            tempObj = faultyMACKRepocitory.findAllByIsDeletedFalse()
                        .stream()
                        .peek(faultyMAC ->
                                faultyMAC.setMackId(faultyMAC.getMackId().toLowerCase().replaceAll("[-:.]", ""))
                        )
                        .collect(Collectors.toMap(FaultyMAC::getMackId, faultyMAC -> faultyMAC));
           faultyMacList = tempObj;
            return faultyMacList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.ACCTPROFILECONFIG_CACHE)
    public List<RadiusProfile> cacheAcctProfileData() {
        try {
            if (radiusProfileListAcct == null) {
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                radiusProfileListAcct = radiusProfileRepository.findByRequestTypeOrderByPriorityDesc("Accounting");
                CacheRetrival cacheRetrival = new CacheRetrival();
                if (!CollectionUtils.isEmpty(radiusProfileListAcct)) {
//                    List<Long> dbMappingIds = radiusProfileListAcct.stream().map(RadiusProfile::getMappingMaster).distinct().map(DBMappingMaster::getMappingMasterId).collect(Collectors.toList());
//                    List<DBMapping> dbMappingData = dbMappingRepository.findAllById(dbMappingIds);//cacheRetrival.getDbMappingData();
                    for (int i = 0; i < radiusProfileListAcct.size(); i++) {
                        ConcurrentMap dbFieldMapping = new ConcurrentHashMap();
                        RadiusProfile radiusProfile = radiusProfileListAcct.get(i);
                        List<DBMapping> dbMappingList1 = dbMappingRepository.findDBMappingByMappingMasterId(radiusProfile.getMappingMaster().getMappingMasterId());//dbMappingData.stream().filter(dbMapping -> dbMapping.getMappingMasterId().equals(radiusProfile.getMappingMaster().getMappingMasterId())).collect(Collectors.toList());
                        for (DBMapping dBMapping : dbMappingList1) {
                            if (radiusProfile.getMappingMaster() != null && radiusProfile.getMappingMaster().getMappingMasterId() == dBMapping.getMappingMasterId()) {
                                dbFieldMapping.put(dBMapping.getRadiusName(), dBMapping.getDbColumnName());
                            }
                        }
                        radiusProfile.setDbFieldMapping(dbFieldMapping);
                    }
                }

            }
            return radiusProfileListAcct;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.VLANMANAGEMENT_CACHE)
    public List<VLANManagement> cacheVlanManagement() {
        try {
            if (vlanManagements == null) {
                //		System.out.println("First time call method implementation to fill up the cache--------Start");
                long start = System.currentTimeMillis();
                vlanManagements = vlanManagementRepository.findAll().stream().sorted(Comparator.comparing(VLANManagement::getPriority).reversed()).collect(Collectors.toList());
                long end = System.currentTimeMillis();
                System.out.println("Total time for vlan fetch :"+ (end-start));
//                for (VLANManagement vlanManagement : vlanManagements) {
//                    List<VLANValidationMapping> mappingList = vlanManagement.getMappingList();
//                    for (VLANValidationMapping vlanValidationMapping : mappingList) {
//                        String attribute = vlanValidationMapping.getRadiusAttribute();
//                        String regex = vlanValidationMapping.getRegex();    //{^(?:\S+\s+){3}key\s+\d+:\d+,{PROFILE{vlan.NAS_PORT_ID_2}}
//                        CustomerData customerData = new CustomerData();
//                        customerData.setVlanManagement(vlanManagement);
//                        if (regex.startsWith("REGEX")) {
//                            log.info("Checking vlan management : " + vlanManagement.getVlanName() + " and Regex: " + regex);
//                            regex = ExpressionEvaluator.parseExpression(regex, "REGEX\\{(.*)\\}");
//                            String[] expPair = regex.split(",");
//                            String key = expPair[1];
//                            key = ExpressionEvaluator.getValueFromGivenExpression(key, customerData, null);
//                            vlanValidationMapping.setRegexValue(key);
//                        } else {
//                            regex = ExpressionEvaluator.getValueFromGivenExpression(regex, customerData, null);
//                            // CONTAINS{REQ{User-Name},REGEX{\b(?:\d{1,3}\.){3}\d{1,3}(?:@[a-zA-Z0-9_]+)?\b}}
//                            String exp = "CONTAINS{REQ{" + attribute + "}," + regex + "}";
//                            vlanValidationMapping.setRegexValue(exp);
//                            log.info("Vlan validation final expression: " + exp + " regex: " + regex);
//                        }
//                    }
//                }
                //		System.out.println("First time call method implementation to fill up the cache--------End");
            }
            return vlanManagements;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    //@Cacheable(cacheNames = AAAConstant.VLANMANAGEMENT_CACHE)
    public Map<String,List<VLANManagement>> cacheVlanManagement1() {
        try {
            Map<String,List<VLANManagement>> vlanManagementsTemp = new HashMap<>();
            if (vlanManagements1 == null || vlanManagements1.size() < 1) {
                List<VLANManagement> collect = vlanManagementRepository.findAllWithMappings().stream().sorted(Comparator.comparing(VLANManagement::getPriority).reversed()).collect(Collectors.toList());
                for (VLANManagement vlanManagement : collect) {
                    List<VLANManagement> orDefault = vlanManagementsTemp.getOrDefault(vlanManagement.getNasPortId4(), null);
                    if(orDefault != null){
                        orDefault.add(vlanManagement);
                    }else {
                        List<VLANManagement> vlanManagementsList = new ArrayList<>();
                        vlanManagementsList.add(vlanManagement);
                        vlanManagementsTemp.put(vlanManagement.getNasPortId4(), vlanManagementsList);
                    }
                }
                vlanManagements1 = vlanManagementsTemp;
            }
//            System.out.println("total time to load new vlan: "+ (end - start));
            return vlanManagements1;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
    //@Cacheable(cacheNames = AAAConstant.VLANMANAGEMENT_CACHE)
    public Map<String,List<VLANManagement>> cacheVlanManagementReload() {
        try {
            Map<String,List<VLANManagement>> vlanManagementsTemp = new HashMap<>();
//            if (vlanManagements1 == null) {
                List<VLANManagement> collect = vlanManagementRepository.findAllWithMappings().stream().sorted(Comparator.comparing(VLANManagement::getPriority).reversed()).collect(Collectors.toList());
                for (VLANManagement vlanManagement : collect) {
                    List<VLANManagement> orDefault = vlanManagementsTemp.getOrDefault(vlanManagement.getNasPortId4(), null);
                    if(orDefault != null){
                        orDefault.add(vlanManagement);
                    }else {
                        List<VLANManagement> vlanManagementsList = new ArrayList<>();
                        vlanManagementsList.add(vlanManagement);
                        vlanManagementsTemp.put(vlanManagement.getNasPortId4(), vlanManagementsList);
                    }
                }
//            }
//            System.out.println("total time to load new vlan: "+ (end - start));
            vlanManagements1 = vlanManagementsTemp;
            return vlanManagements1;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.PERMISSIONS_CACHE)
    public Map<Long, List<String>> cachePermission() {
        try {
            if (cacheList == null) {
                cacheList = new HashMap<>();
                List<Object[]> resultSet = repository.fetchPermissions();

                if (resultSet != null && resultSet.size() > 0) {
                    List<String> operationList = null;
                    Long roleId = 0L;
                    String operation = "";

                    for (int i = 0; i < resultSet.size(); i++) {


                        Object[] resultFields = resultSet.get(i);
                        if (resultFields[0] == null)
                            continue;
                        roleId = Long.valueOf(resultFields[0].toString());
                        operation = resultFields[1].toString();
//						logger.info("Role: " + roleId + " Operation: " + operation);
                        if (operationList == null) {
                            operationList = new ArrayList<>();
                        }
                        operationList.add(operation);
                        cacheList.put(roleId, operationList);
                    }
                }
                log.info("PermissionCache" + " Ended");
            }
            return cacheList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    //@Cacheable(cacheNames = AAAConstant.PERMISSIONS_CACHE)
    public List<ConfigurationService> cacheSystemConfiguration() {
        return configuraionRepository.findAll();
    }

    @Override
    public void reloadCache() {
        log.debug("RELOAD CACHE START..!");
        try {
            clearAllCache();
            proxyMap = null;
            cacheProxyData();
            clientMap = null;
            cacheClientConfig();
            coaDMProfileListReturn = null;
            cacheCoADMProfileData();
            radiusProfileList = null;
            cacheAuthProfileData();
            radiusProfileListDyna = null;
            cachedynaAuthProfileData();
            dbMappingList = null;
            cacheDbMappingData();
            radiusProfileListAcct = null;
            cacheAcctProfileData();
            faultyMacList = null;
            cacheFaultyMacData();
//            vlanManagements = null;
//            cacheVlanManagement();
            vlanManagements1 = null;
            cacheVlanManagement1();
        } catch (Exception ex) {
            log.error("Error to reload Cache");
        }
        log.debug("RELOAD CACHE END..!");
    }

    public void clearAllCache() {
        try {
            cacheManager.getCacheNames().parallelStream().forEach(name -> {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                }
            });
        } catch (Exception ex) {
            log.error("Error to clear Cache");
        }
    }

    private void setClientGroupMapping(List<ClientGroupMapping> clientGroupMappings) {
        for (ClientGroupMapping clientGroupMapping : clientGroupMappings) {

            Optional<ClientGroup> clientGroup = clientGroupRepository.findById(clientGroupMapping.getClientGroupId());
            if (clientGroup.isPresent()) {
                ClientGroup clientGroupData = clientGroup.get();
                try {
                    List<ClientReply> clientReply = clientReplyRepository.findbyClientGroup(clientGroup.get().getClientGroupId());
                    if (!CollectionUtils.isEmpty(clientReply) && "Enable".equalsIgnoreCase(clientGroupData.getAuthenticationProfile())) {
                        List<ClientReply> acceptClientReply = clientReply.stream().filter(clientRep -> !clientRep.isRejectAttribute()).collect(Collectors.toList());
                        clientGroupData.setClientReplyList(acceptClientReply);
                        List<ClientReply> rejectClientReply = clientReply.stream().filter(ClientReply::isRejectAttribute).collect(Collectors.toList());
                        clientGroupData.setClientReplyListForRejectAuthResponse(rejectClientReply);

                    } else {
                        clientGroupData.setClientReplyList(clientReply);
                    }
                    //Set VendorId and Input/output type
                    String inputType = clientGroupData.getInputPacketAttributeValue();
                    String outputType = clientGroupData.getOutputPacketAttributeValue();
                    Optional<DictionaryAttribute> inputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(inputType);
                    inputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                        clientGroupData.setInPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                        clientGroupData.setInPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                    });
                    Optional<DictionaryAttribute> outputDictionaryAttribute = dictionaryAttributeRepository.findByNameEqualsIgnoreCase(outputType);
                    outputDictionaryAttribute.ifPresent(dictionaryAttribute -> {
                        clientGroupData.setOutPutPacketAttributeType(dictionaryAttribute.getAttributeId());
                        clientGroupData.setOutPutPacketAttributeVendorId(dictionaryAttribute.getDictionary().getVendorId());
                    });
                    //Set Access Response messages
                    if (clientGroupData.getAuthenticationProfile() != null && clientGroupData.getAuthenticationProfile().equalsIgnoreCase("Enable")) {
                        List<AccessResponse> accessResponses = accessResponseRepo.findAllByIsDeleteFalse();
                        clientGroupData.setAccessResponses(accessResponses);
                    }
                    //clientData.setClientGroupData(clientGroupData);
                    clientGroupMapping.setClientGroupData(clientGroupData);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error("Exception at get Client group cache: " + ex.getMessage());
                }
            }
        }
    }

    void initializeKeyStore(List<RadiusProfile> cacheAuthProfileData) {

        for (RadiusProfile radiusProfile : cacheAuthProfileData) {
            String authenticationType = radiusProfile.getAuthenticationType();
            if (CommonConstants.AUTH_TYPE_EAP_TLS.equalsIgnoreCase(authenticationType) || CommonConstants.AUTH_TYPE_EAP_TTLS.equalsIgnoreCase(authenticationType) && radiusProfile.getKeyStore() == null) {
                log.debug("Initializing key store for: " + radiusProfile.getName());

                String trustStoreName = null;
                String trustStorePassword = null;
                String keyStoreName = null;
                String keyStorePassword = null;

                if (!radiusProfile.getProfileMappings().isEmpty()) {
                    for (ProfileMapping p : radiusProfile.getProfileMappings()) {
                        if ("jks".equalsIgnoreCase(p.getFileType())) {
                            trustStoreName = p.getFilePath();
                            trustStorePassword = p.getPassword();
                        } else if ("p12".equalsIgnoreCase(p.getFileType())) {
                            keyStoreName = p.getFilePath();
                            keyStorePassword = p.getPassword();

                        }
                    }
                    radiusProfile.setKeyStore(new KeyStoreImpl(keyStoreName, keyStorePassword, trustStoreName, trustStorePassword));
                }
            }
        }
    }

    @Override
    public void reloadCache(String cacheName) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
            switch (cacheName) {
                case AAAConstant.PROXYCACHE:
                    proxyMap = null;
                    cacheProxyData();
                    log.info("Proxy Data Cache reloaded successfully");
                    break;

                case AAAConstant.CLIENTDETAILCACHE:
                    clientMap = cacheClientConfigOnRunTime();
                    log.info("Client Config Data Cache reloaded successfully");
                    break;

                case AAAConstant.COADMPROFILECONFIGCACHE:
                    coaDMProfileListReturn = null;
                    cacheCoADMProfileData();
                    log.info("COA/DM Profile Data Cache reloaded successfully");
                    break;

                case AAAConstant.AUTHPROFILECONFIG_CACHE:
                    radiusProfileList = null;
                    cacheAuthProfileData();
                    log.info("Auth Profile Data Cache reloaded successfully");
                    break;

                case AAAConstant.DYNAAUTHPROFILECONFIG_CACHE:
                    radiusProfileListDyna = null;
                    cachedynaAuthProfileData();
                    log.info("Dynamic Auth Profile Data Cache reloaded successfully");
                    break;

                case AAAConstant.DBMAPPING_CACHE:
                    dbMappingList = null;
                    cacheDbMappingData();
                    log.info("DB Mapping Data Cache reloaded successfully");
                    break;

                case AAAConstant.ACCTPROFILECONFIG_CACHE:
                    radiusProfileListAcct = null;
                    cacheAcctProfileData();
                    log.info("Acct Profile Data Cache reloaded successfully");
                    break;

                case AAAConstant.FAULTY_MAC_CACHE:
                    faultyMacList = cacheFaultyMacDataAtRunTime();
                    log.info("Faulty Mac Cache reloaded successfully");
                    break;
                case AAAConstant.VLANMANAGEMENT_CACHE:
//                    vlanManagements1 = null;
                    cacheVlanManagementReload();
                    break;
                default:
                    log.warn("Unknown cache name: {}", cacheName);
            }
        } catch (Exception ex) {
            log.error("Error while reloading cache for {}: {}", cacheName, ex.getMessage());
        }
    }

}
