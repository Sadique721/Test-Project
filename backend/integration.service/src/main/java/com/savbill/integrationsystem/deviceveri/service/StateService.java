package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.StateData;
import com.savbill.integrationsystem.deviceveri.mapper.StateMapper;
import com.savbill.integrationsystem.deviceveri.model.StateDTO;
import com.savbill.integrationsystem.deviceveri.repository.StateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService extends ExBaseAbstractService<StateDTO, StateData, Long> {


    @Autowired
    private StateRepo repo;

    @Autowired
    private StateMapper mapper;

    public StateService(StateRepo repo, StateMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "StateService[]";
    }
}
