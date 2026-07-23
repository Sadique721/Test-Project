package com.savbill.notification.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.notification.entity.QSystemConfig;
import com.savbill.notification.entity.SystemConfig;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.SystemConfigDTO;
import com.savbill.notification.repository.SystemConfigRepository;
import com.savbill.notification.services.SystemConfigService;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.NotificationUtils;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final Logger log = Logger.getLogger(SystemConfigServiceImpl.class);

    @Autowired
    private SystemConfigRepository configRepository;

    @Override
    public List<SystemConfig> findAllSystemConfig(Long mvnoId, String key, String value, String serviceName) {
        try {
            QSystemConfig qSystemConfig = QSystemConfig.systemConfig;
            BooleanExpression boolExp = qSystemConfig.isNotNull();
            if (mvnoId != 1)
                boolExp = boolExp.and(qSystemConfig.mvnoId.in(1, mvnoId));

            if(ValidateCrudTransactionData.validateStringTypeFieldValue(key))
                boolExp = boolExp.and(qSystemConfig.key.like("%" + key + "%"));
            if(ValidateCrudTransactionData.validateStringTypeFieldValue(value))
                boolExp = boolExp.and(qSystemConfig.configValue.like("%" + value + "%"));
            if(ValidateCrudTransactionData.validateStringTypeFieldValue(serviceName))
                boolExp = boolExp.and(qSystemConfig.serviceName.like("%" + serviceName + "%"));

            return (List<SystemConfig>) configRepository.findAll(boolExp);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SystemConfig findStaffById(Long id, Long mvnoId) {
        Optional<SystemConfig> systemConfig = configRepository.findById(id);
        if(systemConfig.isPresent())
            return systemConfig.get();
        return null;
    }

    @Override
    public List<SystemConfig> searchSystemConfigByKey(String key, Long mvnoId) {
        try {
            if(ValidateCrudTransactionData.validateStringTypeFieldValue(key)) {
                QSystemConfig qSystemConfig = QSystemConfig.systemConfig;
                BooleanExpression boolExp = qSystemConfig.isNotNull();
                if (mvnoId != 1)
                    boolExp = boolExp.and(qSystemConfig.mvnoId.in(1, mvnoId));
                boolExp = boolExp.and(qSystemConfig.key.like("%" + key + "%"));
                List<SystemConfig> systemConfigs = (List<SystemConfig>) configRepository.findAll(boolExp);
                return systemConfigs;
            } else {
                throw new IllegalArgumentException("Given key is not valid: "+key);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SystemConfig saveSystemConfig(SystemConfigDTO configDTO, Long mvnoId, boolean isFromCommon) {
    	try {

            validateConfig(configDTO);
            SystemConfig systemConfig = new SystemConfig(configDTO, mvnoId);
            if(!isFromCommon)
            	systemConfig.setCreatedBy(MDC.get("userName").toString());
            else
            	systemConfig.setCreatedBy(configDTO.getCreatedBy());
            if(isFromCommon)
            	log.debug("System config save succefully from common, key: "+configDTO.getKey());
        	log.debug("System config save succefully, key: "+configDTO.getKey()+" user: "+MDC.get("userName"));
            return configRepository.save(systemConfig);
		} catch (Exception e) {
			log.error("System Config details update failed, " + "Request : { ERROR : "+e.getMessage() + " }");
            log.error("TRACE ERROR : " + NotificationUtils.GetError(e));
            throw new RuntimeException(e.getMessage());
		}
    }

    @Override
    public SystemConfig updateSystemConfig(SystemConfigDTO configDTO, Long mvnoId, boolean isFromCommon) {
        try {
            if(!ValidateCrudTransactionData.validateLongTypeFieldValue(configDTO.getId())) {
                throw new IllegalArgumentException(NotificationConstants.BASIC_STRING_MSG + "Please enter valid Id.");
            }
            validateConfig(configDTO);
            Optional<SystemConfig> config = configRepository.findByKeyAndServiceName(configDTO.getKey(), configDTO.getServiceName());
            if(config.isPresent()) {
                if(ValidateCrudTransactionData.validateStringTypeFieldValue(configDTO.getConfigValue()))
                    config.get().setConfigValue(configDTO.getConfigValue());
                
                config.get().setLastModifiedDate(LocalDateTime.now());
                
                if(!isFromCommon)
                	config.get().setLastModifiedBy(MDC.get("userName").toString());
                else
                	config.get().setLastModifiedBy(configDTO.getLastModifiedBy());
                
                if(isFromCommon)
                	log.debug("System config update succefully from common, key: "+configDTO.getKey());
            	log.debug("System config update succefully, key: "+configDTO.getKey()+" user: "+MDC.get("userName"));
                return configRepository.saveAndFlush(config.get());
            } else {
                throw new CustomException("System config not available for given key: "+configDTO.getKey()+" and serviceName:"+configDTO.getServiceName(), HttpStatus.NOT_FOUND.value());
            }
        } catch (CustomException e) {
        	log.error("System Config details update failed, " + "Request : { ERROR : "+e.getMessage() + " }");
            log.error("TRACE ERROR : " + NotificationUtils.GetError(e));
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean validateConfig(SystemConfigDTO configDTO) {
        if(!ValidateCrudTransactionData.validateStringTypeFieldValue(configDTO.getKey())) {
            throw new IllegalArgumentException(NotificationConstants.BASIC_STRING_MSG + "Please enter valid key.");
        } else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(configDTO.getServiceName())) {
            throw new IllegalArgumentException(NotificationConstants.BASIC_STRING_MSG + "Please enter Service name.");
        } else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(configDTO.getConfigValue())) {
            throw new IllegalArgumentException(NotificationConstants.BASIC_STRING_MSG + "Please enter valid value.");
        }
        return true;
    }
    
    @Override
    public void saveAndUpdateSystemConfigFromCommon(SystemConfigDTO configDto, boolean isUpdate) {
    	if(isUpdate) {
    		updateSystemConfig(configDto, configDto.getMvnoId(), true);
    	} else {
    		saveSystemConfig(configDto, configDto.getMvnoId(), true);
    	}
    }
}
