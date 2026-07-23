package com.savbill.radius.services.impl;

import com.savbill.radius.entity.ConfigurationService;
import com.savbill.radius.kafka.message.SaveClientServMessge;
import com.savbill.radius.repository.ConfigurationServiceRepository;
import com.savbill.radius.services.ConfigurationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigurationServicesImpl implements ConfigurationServices {

    @Autowired
    private ConfigurationServiceRepository serviceRepository;

    @Override
    public void addUpdateConfiguration(SaveClientServMessge message) {
        if(message.getId() != null) {
            Optional<ConfigurationService> systemConfigurationMessage = serviceRepository.findByNameAndMvnoId(message.getName(), message.getMvnoId());
            if(systemConfigurationMessage.isPresent()) {
                updateConfigurationService(message, systemConfigurationMessage.get());
            } else {
                addConfigurationService(message);
            }
        }
    }

    @Override
    public ConfigurationService addConfigurationService(SaveClientServMessge message) {
        ConfigurationService service = new ConfigurationService(message);
        return serviceRepository.save(service);
    }

    @Override
    public ConfigurationService updateConfigurationService(SaveClientServMessge message, ConfigurationService configurationService) {
        ConfigurationService service = new ConfigurationService(message, configurationService.getId());
        serviceRepository.save(service);
        return null;
    }
}
