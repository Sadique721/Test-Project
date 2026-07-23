package com.savbill.ticketmanagement.core.modules.Mail.service;

import com.savbill.ticketmanagement.core.modules.Mail.domain.ReceiveEmailConfiguration;
import com.savbill.ticketmanagement.core.modules.Mail.model.ReceiveEmailConfigurationDTO;
import com.savbill.ticketmanagement.core.modules.Mail.repository.ReceiveEmailConfigurationRepository;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiveEmailConfigurationService {

    @Autowired
    ReceiveEmailConfigurationRepository receiveEmailConfigurationRepository;

    public List<ReceiveEmailConfiguration> getAllConfigurations(){
        List<ReceiveEmailConfiguration> configurationList = receiveEmailConfigurationRepository.findAll();
        return configurationList;
    }

    public ReceiveEmailConfiguration getByName(String name){
        ReceiveEmailConfiguration configuration = receiveEmailConfigurationRepository.findByName(name);
        return configuration;
    }

    @Transactional
    public ReceiveEmailConfigurationDTO saveReceiveEmailConfig(ReceiveEmailConfigurationDTO receiveEmailConfigDTO){
        ReceiveEmailConfiguration receiveEmailConfig = DtoToDomain(receiveEmailConfigDTO);      /**convert  dto to domain here**/
        ReceiveEmailConfiguration savedReceiveEmailConfig = receiveEmailConfigurationRepository.save(receiveEmailConfig);
        ReceiveEmailConfigurationDTO returnReceiveEmailConfigDto = DomainToDto(savedReceiveEmailConfig);
        return returnReceiveEmailConfigDto;
    }

    public ReceiveEmailConfiguration DtoToDomain(ReceiveEmailConfigurationDTO receiveEmailConfigDTO){
        ReceiveEmailConfiguration receiveEmailConfiguration = new ReceiveEmailConfiguration();
        receiveEmailConfiguration.setName(receiveEmailConfigDTO.getName());
        receiveEmailConfiguration.setIsDelete(receiveEmailConfigDTO.getIsDelete());
        receiveEmailConfiguration.setName(receiveEmailConfigDTO.getName());
        receiveEmailConfiguration.setUserName(receiveEmailConfigDTO.getUserName());
        receiveEmailConfiguration.setPassword(receiveEmailConfigDTO.getPassword());
        receiveEmailConfiguration.setHost(receiveEmailConfigDTO.getHost());
        receiveEmailConfiguration.setPort(receiveEmailConfigDTO.getPort());
        receiveEmailConfiguration.setIsEnable(receiveEmailConfigDTO.getIsEnable());
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        receiveEmailConfiguration.setMvnoId(Long.valueOf(mvnoId));
        List<Long> buids = getBUIdsFromCurrentStaff();
        if(buids != null && !buids.isEmpty()){
            receiveEmailConfiguration.setBuId(buids.get(0));
        }else{
            receiveEmailConfiguration.setBuId(receiveEmailConfigDTO.getBuId());
        }
        if(receiveEmailConfigDTO.getIsDelete() == null){
            receiveEmailConfiguration.setIsDelete(false);
        }
        else{
            receiveEmailConfiguration.setIsDelete(receiveEmailConfigDTO.getIsDelete());
        }
        if(receiveEmailConfigDTO.getIsEnable() != null){
            receiveEmailConfiguration.setIsEnable(receiveEmailConfigDTO.getIsEnable());
        }
        else{
            receiveEmailConfiguration.setIsEnable(true);
        }
    return receiveEmailConfiguration;
    }

    public ReceiveEmailConfigurationDTO DomainToDto(ReceiveEmailConfiguration receiveEmailConfig){
        ReceiveEmailConfigurationDTO receiveEmailConfigurationDTO = new ReceiveEmailConfigurationDTO();
        receiveEmailConfigurationDTO.setId(receiveEmailConfig.getId());
        receiveEmailConfigurationDTO.setIsDelete(receiveEmailConfig.getIsDelete());
        receiveEmailConfigurationDTO.setIsEnable(receiveEmailConfig.getIsEnable());
        receiveEmailConfigurationDTO.setName(receiveEmailConfig.getName());
        receiveEmailConfigurationDTO.setIsDelete(receiveEmailConfig.getIsDelete());
        receiveEmailConfigurationDTO.setName(receiveEmailConfig.getName());
        receiveEmailConfigurationDTO.setUserName(receiveEmailConfig.getUserName());
        receiveEmailConfigurationDTO.setPassword(receiveEmailConfig.getPassword());
        receiveEmailConfigurationDTO.setHost(receiveEmailConfig.getHost());
        receiveEmailConfigurationDTO.setPort(receiveEmailConfig.getPort());
        receiveEmailConfigurationDTO.setIsEnable(receiveEmailConfig.getIsEnable());
        receiveEmailConfigurationDTO.setMvnoId(receiveEmailConfig.getMvnoId());
        receiveEmailConfigurationDTO.setBuId(receiveEmailConfig.getBuId());
        return receiveEmailConfigurationDTO;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

}
