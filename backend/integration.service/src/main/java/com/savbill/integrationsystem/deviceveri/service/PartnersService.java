package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.PartnersData;
import com.savbill.integrationsystem.deviceveri.mapper.PartnersMapper;
import com.savbill.integrationsystem.deviceveri.model.PartnersDTO;
import com.savbill.integrationsystem.deviceveri.repository.PartnersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnersService extends ExBaseAbstractService<PartnersDTO, PartnersData, Long> {


    @Autowired
    private PartnersRepo repo;

    @Autowired
    private PartnersMapper mapper;

    public PartnersService(PartnersRepo repo, PartnersMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "PartnersService[]";
    }
}
