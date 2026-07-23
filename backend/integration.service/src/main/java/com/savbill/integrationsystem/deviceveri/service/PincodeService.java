package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.PincodeData;
import com.savbill.integrationsystem.deviceveri.mapper.PincodeMapper;
import com.savbill.integrationsystem.deviceveri.model.PincodeDTO;
import com.savbill.integrationsystem.deviceveri.repository.PincodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PincodeService extends ExBaseAbstractService<PincodeDTO, PincodeData, Long> {


    @Autowired
    private PincodeRepo repo;

    @Autowired
    private PincodeMapper mapper;

    public PincodeService(PincodeRepo repo, PincodeMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "PincodeService[]";
    }
}
