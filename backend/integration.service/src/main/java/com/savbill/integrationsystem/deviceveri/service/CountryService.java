package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CountryData;
import com.savbill.integrationsystem.deviceveri.mapper.CountryMapper;
import com.savbill.integrationsystem.deviceveri.model.CountryDTO;
import com.savbill.integrationsystem.deviceveri.repository.CountryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService extends ExBaseAbstractService<CountryDTO, CountryData, Long> {


    @Autowired
    private CountryRepo repo;

    @Autowired
    private CountryMapper mapper;

    public CountryService(CountryRepo repo, CountryMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CountryService[]";
    }
}
