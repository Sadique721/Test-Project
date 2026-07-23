package com.savbill.cpm.modules.TimeBasePolicy.service;

import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.savbill.cpm.modules.TimeBasePolicy.mapper.CustomerTimebasePolicyDetailsMapper;
import com.savbill.cpm.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import com.savbill.cpm.modules.TimeBasePolicy.repository.CustomerTimeBasePolicyDetailsRepository;
import com.savbill.cpm.rabbitMq.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CustomerTimeBasePolicyDetailsService extends ExBaseAbstractService2<TimeBasePolicyDetailsDTO, TimeBasePolicyDetails, Long> {
    public CustomerTimeBasePolicyDetailsService(CustomerTimeBasePolicyDetailsRepository repository, CustomerTimebasePolicyDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Autowired
    private MessageSender messageSender;
}
