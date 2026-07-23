package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CityData;
import com.savbill.integrationsystem.deviceveri.mapper.CityMapper;
import com.savbill.integrationsystem.deviceveri.model.CityDTO;
import com.savbill.integrationsystem.deviceveri.repository.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService extends ExBaseAbstractService<CityDTO, CityData, Long> {


    @Autowired
    private CityRepo repo;

    @Autowired
    private CityMapper mapper;

    public CityService(CityRepo repo, CityMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CityService[]";
    }
}
