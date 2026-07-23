package com.savbill.radius.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

import com.savbill.radius.entity.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.ClientService;
import com.savbill.radius.utils.*;
import com.savbill.radius.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.RadiusProfileDto;
import com.savbill.radius.services.CoaDMProfileService;
import com.savbill.radius.services.RadiusProfileService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

@Service
public class RadiusProfileServiceImpl implements RadiusProfileService {

    private static final String ACCOUNTING = "Accounting";
    private static final String AUTHENTICATION = "Authentication";
    private static final String DM = "DM";
    private static final String COA = "CoA";
    private static final String NONE = "None";
    private static final String DISABLE = "Disable";
    private static final String ENABLE = "Enable";

    @Autowired
    private RadiusProfileRepository radiusProfileRepository;

    @Autowired
    private ProxyServerRepository proxyServerRepository;

    @Autowired
    private CoaDMProfileService coaDMProfileService;

    @Autowired
    private DBMappingMasterRepository dbMappingMasterRepository;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;
    @Autowired
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(RadiusProfileServiceImpl.class);
    @Autowired
    private AuthModeAttributeMappingRepository authModeAttributeMappingRepository;

    private String PATH;

    @Autowired
    private ClientService clientService;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private ProfileMappingRepository profileMappingRepository;

    @Override
    public RadiusProfile findByName(String name, Integer mvnoId) {
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
            throw new IllegalArgumentException("Please enter valid proxy server id.");
        QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
        BooleanExpression boolExp = qRadiusProfile.isNotNull();
        boolExp = boolExp.and(qRadiusProfile.name.eq(name));
        if (mvnoId == null || mvnoId != 1)
            boolExp = boolExp.and(qRadiusProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
        Optional<RadiusProfile> optionalRadiusProfile = radiusProfileRepository.findOne(boolExp);
//		if (!optionalRadiusProfile.isPresent()) {
//			throw new IllegalArgumentException(
//					"No record found with Radius profile name " + name + " . Please enter valid radius profile name.");
//		}
        return optionalRadiusProfile.get();
    }

    @Override
    public List<RadiusProfile> searchByName(String name, Integer mvnoId) {
        try {
            if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
                name = "";
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression boolExp = qRadiusProfile.isNotNull();
            boolExp = boolExp.and(qRadiusProfile.name.containsIgnoreCase(name));
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qRadiusProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            List radiusProfileList = (List<RadiusProfile>) radiusProfileRepository.findAll(boolExp);
//			if(radiusProfileList.isEmpty())
//			{
//				throw new IllegalArgumentException(
//						"No record found by with radius profile name: "+name+" Please enter valid radius name");
//			}
            return radiusProfileList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RadiusProfile findById(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid Radius profile id.");
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression exp = qRadiusProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                exp = exp.and(qRadiusProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            exp = exp.and(qRadiusProfile.radiusProfileId.eq(id));

            Optional<RadiusProfile> acctProfile = radiusProfileRepository.findOne(exp);
            RadiusProfile radiusProfile = acctProfile.get();
            List<ProfileMapping> profileMappings = profileMappingRepository.findByProfileId(id);
            radiusProfile.setProfileMappings(profileMappings);
            if (radiusProfile==null) {
                throw new IllegalArgumentException(
                        "No record found with Radius profile id " + id + " . Please enter valid radius profile id.");
            }
            return radiusProfile;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public RadiusProfile validateRadiusProfileForUpdateDelete(String name, Integer mvnoId) {
        try {
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression exp = qRadiusProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                exp = exp.and(qRadiusProfile.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            exp = exp.and(qRadiusProfile.name.eq(name));

            Optional<RadiusProfile> acctProfile = radiusProfileRepository.findOne(exp);
            if (!acctProfile.isPresent()) {
                throw new RuntimeException("Radius Profile not found with name " + name + " or You do not have access to update or delete this record.");
            }
            return acctProfile.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<RadiusProfile> findByProxyServerId(Long proxyServerId, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(proxyServerId))
                throw new IllegalArgumentException("Please enter valid proxy server id.");
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression boolExp = qRadiusProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qRadiusProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qRadiusProfile.proxyServer.id.eq(proxyServerId));
            return (List<RadiusProfile>) radiusProfileRepository.findAll(boolExp);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<RadiusProfile> findAll(Integer mvnoId) {
        try {
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression boolExp = qRadiusProfile.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return radiusProfileRepository.findAll();
            else {
                boolExp = boolExp.and(qRadiusProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<RadiusProfile>) radiusProfileRepository.findAll(boolExp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            RadiusProfile radiusProfile = findById(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            validateRadiusProfileForUpdateDelete(radiusProfile.getName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
        //    log.info("Radius Profile deleted successfully: " + radiusProfile.getName());
            radiusProfileRepository.deleteById(id);
        } catch (RuntimeException e) {
          //  log.error("Error while deleting Radius Profile: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public RadiusProfile save(RadiusProfileDto radiusProfileDto, Integer mvnoId,MultipartFile[] trustStoreFile , MultipartFile[] keyStoreFile) {
        try {
            RadiusProfile radiusProfileVo = validateRadiusProfileData(radiusProfileDto, false, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getProxyServerName())) {
                ProxyServer proxyServer = validateProxyServer(radiusProfileDto.getProxyServerName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
                radiusProfileVo.setProxyServer(proxyServer);
            }
//            if(radiusProfileDto.getAuthenticationSubType()!=null)
//            {
//                radiusProfileVo.setAuthenticationType(radiusProfileDto.getAuthenticationSubType());
//            }else{
//                radiusProfileVo.setAuthenticationType(radiusProfileDto.getAuthenticationType());
//            }
            radiusProfileVo.setCreatedOn(new Timestamp(new Date().getTime()));
            radiusProfileVo.setLastModifiedOn(new Timestamp(new Date().getTime()));

            RadiusProfile savedRadiusProfile = radiusProfileRepository.save(radiusProfileVo);
            PATH=clientService.getClientSrvByName(RadiusConstants.PDF_PATH,mvnoId)
                    .stream()
                    .filter(i -> i.getMvnoId().equals(mvnoId))
                    .findFirst()
                    .map(ClientServiceEntity::getValue)
                    .orElse(null);

            String subFolderName = File.separator + radiusProfileDto.getName().trim() + File.separator;
            String path = PATH + subFolderName;

            String[] fileNames = {radiusProfileDto.getTrustStoreDoc(), radiusProfileDto.getKeystoreDoc()};
            MultipartFile[][] files = {trustStoreFile, keyStoreFile};

            // Process each file and save it if present
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i] != null && files[i] != null && files[i].length > 0) {
                    for (MultipartFile file : files[i]) {
                        if (file != null && !file.isEmpty()) {
                            String fileName = extractFilename(fileNames[i]);
                            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                            ProfileMapping profileMapping = new ProfileMapping();
                            profileMapping.setFilePath(path + fileUtility.saveFileToServer(file, path, mvnoId));
                            profileMapping.setProfileId(savedRadiusProfile.getRadiusProfileId());
                            profileMapping.setPassword(radiusProfileDto.getTrustStorePassword());
                            profileMapping.setFileType(fileType);
                            if(fileType.equalsIgnoreCase("jks")){
                                profileMapping.setPassword(radiusProfileDto.getTrustStorePassword());
                            }else{
                                profileMapping.setPassword(radiusProfileDto.getKeystorePassword());

                            }

                            profileMappingRepository.save(profileMapping);
                        }
                    }
                }
            }

            return savedRadiusProfile;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RadiusProfile update(RadiusProfileDto radiusProfileDto, Integer mvnoId, HttpServletRequest request, MultipartFile[] trustStoreFile , MultipartFile[] keyStoreFile) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            RadiusProfile profile = findByName(radiusProfileDto.getName(), mvnoId);
            validateRadiusProfileForUpdateDelete(radiusProfileDto.getName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            RadiusProfile radiusProfileVo = validateRadiusProfileData(radiusProfileDto, true, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            radiusProfileVo.setProfileMappings(profile.getProfileMappings());
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getProxyServerName())) {
                ProxyServer proxyServer = validateProxyServer(radiusProfileDto.getProxyServerName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
                radiusProfileVo.setProxyServer(proxyServer);
            }
            try {
                if(!CollectionUtils.isEmpty(profile.getAuthModeAttributeMappings())) {
                    List<AuthModeAttributeMapping> authModeAttributeMappings = profile.getAuthModeAttributeMappings();
                    authModeAttributeMappingRepository.deleteInBatch(authModeAttributeMappings);
                }
            } catch (Exception ex) {
                log.error("Unable to delete Auth Mode attribute for profile: "+profile.getName());
            }
//            if(radiusProfileDto.getAuthenticationSubType()!=null)
//            {
//                radiusProfileVo.setAuthenticationType(radiusProfileDto.getAuthenticationSubType());
//            }else{
//                radiusProfileVo.setAuthenticationType(radiusProfileDto.getAuthenticationType());
//            }
            radiusProfileVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
            String updated = updateDiffFinder.getUpdatedDiff(profile, radiusProfileVo);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Proxy server has been updated successfully , updated values,"+updated+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
         //   log.info("Radius Profile updated succefully, updated values " + updated);
            RadiusProfile updatedRadiusProfile =  radiusProfileRepository.save(radiusProfileVo);

            if (radiusProfileDto.getAuthenticationType().equalsIgnoreCase("EAP-TTLS") || radiusProfileDto.getAuthenticationType().equalsIgnoreCase("EAP-TLS")) {
                PATH=clientService.getClientSrvByName(RadiusConstants.PDF_PATH,mvnoId)
                        .stream()
                        .filter(i -> i.getMvnoId().equals(mvnoId))
                        .findFirst()
                        .map(ClientServiceEntity::getValue)
                        .orElse(null);

                String subFolderName = File.separator + radiusProfileDto.getName().trim() + File.separator;
                String path = PATH + subFolderName;

                String[] fileNames = {radiusProfileDto.getTrustStoreDoc(), radiusProfileDto.getKeystoreDoc()};
                MultipartFile[][] files = {trustStoreFile, keyStoreFile};

                for (int i = 0; i < fileNames.length; i++) {
                    if (fileNames[i] != null && files[i] != null && files[i].length > 0) {
                        ProfileMapping jksProfileMapping = new ProfileMapping();
                        ProfileMapping p12ProfileMapping = new ProfileMapping();
                        if(radiusProfileVo.getProfileMappings() != null && !radiusProfileVo.getProfileMappings().isEmpty()){
                            jksProfileMapping = radiusProfileVo.getProfileMappings().stream().filter(mapping -> mapping.getFileType().equalsIgnoreCase("jks")).findFirst().get();
                            p12ProfileMapping = radiusProfileVo.getProfileMappings().stream().filter(mapping -> mapping.getFileType().equalsIgnoreCase("p12")).findFirst().get();
                        }

                        for (MultipartFile file : files[i]) {
                            if (file != null && !file.isEmpty()) {
                                String fileName = extractFilename(fileNames[i]);
                                String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
//                            profileMapping.setFilePath(fileUtility.saveFileToServer(file, path, mvnoId));
//                            profileMapping.setProfileId(updatedRadiusProfile.getRadiusProfileId());
                                if(fileType.equalsIgnoreCase("jks")){
                                    jksProfileMapping.setPassword(radiusProfileDto.getTrustStorePassword());
                                    jksProfileMapping.setFileType(fileType);
                                    jksProfileMapping.setFilePath(path + fileUtility.saveFileToServer(file, path, mvnoId));
                                    jksProfileMapping.setProfileId(updatedRadiusProfile.getRadiusProfileId());
                                    profileMappingRepository.save(jksProfileMapping);
                                }else{
                                    p12ProfileMapping.setPassword(radiusProfileDto.getKeystorePassword());
                                    p12ProfileMapping.setFileType(fileType);
                                    p12ProfileMapping.setFilePath(path + fileUtility.saveFileToServer(file, path, mvnoId));
                                    p12ProfileMapping.setProfileId(updatedRadiusProfile.getRadiusProfileId());
                                    profileMappingRepository.save(p12ProfileMapping);
                                }
                            }
                        }
                    }
                }
            }else{
                profileMappingRepository.deleteInBatch(updatedRadiusProfile.getProfileMappings());
            }
            return updatedRadiusProfile;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private CoaDMProfile validateCoaDMProfile(String name, Integer mvnoId) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                return coaDMProfileService.validateCoaDMProfileByName(name, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private DBMappingMaster validateMappingMaster(String name) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                List<DBMappingMaster> dbMappingMasterOptional = dbMappingMasterRepository.findByName(name);
                if (!dbMappingMasterOptional.isEmpty()) {
                    return dbMappingMasterOptional.get(0);
                } else {
                    throw new IllegalArgumentException("No record found with DBMappingMaster name : '" + name
                            + "', Please enter valid DBMappingMaster name");
                }
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private ProxyServer validateProxyServer(String name, Integer mvnoId) {

        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException("Please enter valid proxy server name.");
            QProxyServer qProxyServer = QProxyServer.proxyServer;
            BooleanExpression boolExp = qProxyServer.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qProxyServer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qProxyServer.name.eq(name));
            Optional<ProxyServer> proxyServerOptional = proxyServerRepository.findOne(boolExp);
            if (proxyServerOptional.isPresent()) {
                return proxyServerOptional.get();
            } else {
                throw new IllegalArgumentException("No record found with proxy server name : '" + name
                        + "', Please enter valid proxy server name");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private RadiusProfile validateRadiusProfileData(RadiusProfileDto radiusProfileDto, boolean isUpdate, Integer mvnoId) {
        try {
            ProxyServer proxyServer = null;
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getProxyServerName())) {
                proxyServer = validateProxyServer(radiusProfileDto.getProxyServerName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            }
            //CoaDMProfile coaDMProfile = validateCoaDMProfile(radiusProfileDto.getCoaDMProfile(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            DBMappingMaster mappingMaster = validateMappingMaster(radiusProfileDto.getMappingMaster());
            RadiusProfile radiusProfileVo = new RadiusProfile(radiusProfileDto, proxyServer, mappingMaster);
            radiusProfileVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
//			if (!radiusProfileDto.getAccountCdrStatus().equals(ENABLE)
//				&& !radiusProfileDto.getAccountCdrStatus().equals(DISABLE)) {
//			throw new IllegalArgumentException(
//				"Please enter valid account cdr status. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
//			} else if (!radiusProfileDto.getAuthAudit().equals(ENABLE)
//				&& !radiusProfileDto.getAuthAudit().equals(DISABLE)) {
//			throw new IllegalArgumentException(
//				"Please enter valid auth audit value. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
//			}
//			else if (!radiusProfileDto.getCoadm().equals(NONE) && !radiusProfileDto.getCoadm().equals(COA)
//				&& !radiusProfileDto.getCoadm().equals(DM)) {
//			throw new IllegalArgumentException(
//				"Please enter valid coa value. It should be '" + NONE + "' or '" + COA + "'or '" + DM + "'.");
//			}
//		  else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getName())) {
//			throw new IllegalArgumentException(
//				RadiusConstants.BASIC_STRING_MSG + "Please enter valid radius profile name");
//			} else if (!radiusProfileDto.getRequestType().equals(AUTHENTICATION)
//				&& !radiusProfileDto.getRequestType().equals(ACCOUNTING)) {
//			throw new IllegalArgumentException("Please enter valid request type. It should be '" + AUTHENTICATION
//				+ "' or '" + ACCOUNTING + "'.");
//			} else if (!radiusProfileDto.getSessionStatus().equals(ENABLE)
//				&& !radiusProfileDto.getSessionStatus().equals(DISABLE)) {
//			throw new IllegalArgumentException(
//				"Please enter valid session status. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
//			} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(radiusProfileDto.getPriority())) {
//			throw new IllegalArgumentException(
//				RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid priority value");
//			} else if (!radiusProfileDto.getStatus().equals(RadiusConstants.ACTIVE)
//				&& !radiusProfileDto.getStatus().equals(RadiusConstants.IN_ACTIVE)) {
//			throw new IllegalArgumentException("Please enter valid status. It should be '" + RadiusConstants.ACTIVE
//				+ "' or '" + RadiusConstants.IN_ACTIVE + "'");
//			} else
            if (isUpdate) {
                RadiusProfile radiusProfile = findByName(radiusProfileDto.getName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
                radiusProfileVo.setCreatedOn(radiusProfile.getCreatedOn());
                radiusProfileVo.setRadiusProfileId(radiusProfile.getRadiusProfileId());
            } else if (!isUpdate && radiusProfileDto.getName() != null) {
                checkDuplicateRadiusProfile(radiusProfileDto.getName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            }
            return radiusProfileVo;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private RadiusProfile validateRadiusProfileData(RadiusProfileDto radiusProfileDto, boolean isUpdate) {
        try {
            ProxyServer proxyServer = null;
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getProxyServerName())) {
                proxyServer = validateProxyServer(radiusProfileDto.getProxyServerName(), null);
            }
            //CoaDMProfile coaDMProfile = validateCoaDMProfile(radiusProfileDto.getCoaDMProfile(), null);
            DBMappingMaster mappingMaster = validateMappingMaster(radiusProfileDto.getMappingMaster());
            RadiusProfile radiusProfileVo = new RadiusProfile(radiusProfileDto, proxyServer, mappingMaster);
            if (!radiusProfileDto.getAccountCdrStatus().equals(ENABLE)
                    && !radiusProfileDto.getAccountCdrStatus().equals(DISABLE)) {
                throw new IllegalArgumentException(
                        "Please enter valid account cdr status. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
            } else if (!radiusProfileDto.getAuthAudit().equals(ENABLE)
                    && !radiusProfileDto.getAuthAudit().equals(DISABLE)) {
                throw new IllegalArgumentException(
                        "Please enter valid auth audit value. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
            }  else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getName())) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_STRING_MSG + "Please enter valid radius profile name");
            } else if (!radiusProfileDto.getRequestType().equals(AUTHENTICATION)
                    && !radiusProfileDto.getRequestType().equals(ACCOUNTING)) {
                throw new IllegalArgumentException("Please enter valid request type. It should be '" + AUTHENTICATION
                        + "' or '" + ACCOUNTING + "'.");
            } else if (!radiusProfileDto.getSessionStatus().equals(ENABLE)
                    && !radiusProfileDto.getSessionStatus().equals(DISABLE)) {
                throw new IllegalArgumentException(
                        "Please enter valid session status. It should be '" + ENABLE + "' or '" + DISABLE + "'.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(radiusProfileDto.getPriority())) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid priority value");
            } else if (!radiusProfileDto.getStatus().equals(RadiusConstants.ACTIVE)
                    && !radiusProfileDto.getStatus().equals(RadiusConstants.IN_ACTIVE)) {
                throw new IllegalArgumentException("Please enter valid status. It should be '" + RadiusConstants.ACTIVE
                        + "' or '" + RadiusConstants.IN_ACTIVE + "'");
            } else if (isUpdate) {
                Optional<RadiusProfile> profileOptional = radiusProfileRepository
                        .findByName(radiusProfileDto.getName());
                if (!profileOptional.isPresent()) {
                    throw new IllegalArgumentException("No record found with name : '" + radiusProfileDto.getName()
                            + "', Please enter valid readius profile name to update the record");
                } else {
                    radiusProfileVo.setCreatedOn(profileOptional.get().getCreatedOn());
                    radiusProfileVo.setRadiusProfileId(profileOptional.get().getRadiusProfileId());
                }
            } else if (!isUpdate && radiusProfileDto.getName() != null) {
                List<Object[]> list = radiusProfileRepository
                        .checkForDuplicateReadiusProfile(radiusProfileDto.getName());
                if (!list.isEmpty()) {
                    throw new IllegalArgumentException("Radius profile already exist with name : '"
                            + radiusProfileDto.getName() + "', Please enter unique radius profile name.");
                }
            }
            return radiusProfileVo;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public String changeRadiusProfileStatus(String name, String status, Integer mvnoId,HttpServletRequest request) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG
                        + "Name is mandatory. Please enter valid radius profile name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG
                        + "Radius profile status is mandatory. Please enter valid radius profile status.");
            } else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) {
                throw new IllegalArgumentException("Please enter valid radius profile status. It should be '"
                        + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
            }
            RadiusProfile radiusProfile = validateRadiusProfileForUpdateDelete(name, ValidateCrudTransactionData.validateMvnoId(mvnoId));
             String oldstatus=radiusProfile.getStatus();
            radiusProfile.setStatus(status);
            radiusProfile.setLastModifiedOn(new Timestamp(new Date().getTime()));
            radiusProfileRepository.save(radiusProfile);
            String msg = "";
            if (status.equals(RadiusConstants.ACTIVE)) {
                msg = "Radius profile '" + radiusProfile.getName() + "' has been activated successfully.";
            } else {
                msg = "Radius profile '" + radiusProfile.getName() + "' has been inactivated successfully.";
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Radius Profiles Status has been updated successfully "+oldstatus+" updated to "+status + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return msg;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void checkDuplicateRadiusProfile(String name, Integer mvnoId) {

        try {
            QRadiusProfile qRadiusProfile = QRadiusProfile.radiusProfile;
            BooleanExpression boolExp = qRadiusProfile.isNotNull();

            String msg = "Radius profile already exist with name : '" + name + "', Please enter unique radius profile name." + RadiusConstants.NOT_PUT_IN_QUEUE;

            if (mvnoId == 1) {
                boolExp = boolExp.and(qRadiusProfile.name.eq(name));
                List<RadiusProfile> radiusProfileList = (List<RadiusProfile>) radiusProfileRepository.findAll(boolExp);
                if (!radiusProfileList.isEmpty()) {
                    throw new IllegalArgumentException(msg);
                }
            } else {
                boolExp = boolExp.and(qRadiusProfile.name.eq(name)).and((qRadiusProfile.mvnoId.eq(mvnoId)).or(qRadiusProfile.mvnoId.eq(1)));
                Optional<RadiusProfile> optionalRadiusProfile = radiusProfileRepository.findOne(boolExp);
                if (optionalRadiusProfile.isPresent()) {
                    throw new IllegalArgumentException(msg);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public static String extractFilename(String filepath) {
        Path path = Paths.get(filepath);
        return path.getFileName().toString();
    }
}
