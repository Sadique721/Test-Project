package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.AreaData;
import com.savbill.integrationsystem.deviceveri.mapper.AreaMapper;
import com.savbill.integrationsystem.deviceveri.model.AreaDTO;
import com.savbill.integrationsystem.deviceveri.repository.AreaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService extends ExBaseAbstractService<AreaDTO, AreaData, Long> {


    @Autowired
    private AreaRepo repo;

    @Autowired
    private AreaMapper mapper;

    public AreaService(AreaRepo repo, AreaMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "AreaService[]";
    }
}
