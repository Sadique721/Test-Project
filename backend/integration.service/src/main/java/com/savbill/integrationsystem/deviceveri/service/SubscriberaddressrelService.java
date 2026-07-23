package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.SubscriberaddressrelData;
import com.savbill.integrationsystem.deviceveri.mapper.SubscriberaddressrelMapper;
import com.savbill.integrationsystem.deviceveri.model.SubscriberaddressrelDTO;
import com.savbill.integrationsystem.deviceveri.repository.SubscriberaddressrelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberaddressrelService extends ExBaseAbstractService<SubscriberaddressrelDTO, SubscriberaddressrelData, Long> {


    @Autowired
    private SubscriberaddressrelRepo repo;

    @Autowired
    private SubscriberaddressrelMapper mapper;

    public SubscriberaddressrelService(SubscriberaddressrelRepo repo, SubscriberaddressrelMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "SubscriberaddressrelService[]";
    }
}
