package com.savbill.radius.services;


import com.savbill.radius.entity.ConfigurationService;
import com.savbill.radius.kafka.message.SaveClientServMessge;

public interface ConfigurationServices {

    void addUpdateConfiguration(SaveClientServMessge message);

    ConfigurationService addConfigurationService(SaveClientServMessge message);

    ConfigurationService updateConfigurationService(SaveClientServMessge message, ConfigurationService configurationService);
}
