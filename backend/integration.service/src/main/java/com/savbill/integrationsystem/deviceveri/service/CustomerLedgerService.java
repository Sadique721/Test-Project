package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CustomerLedgerData;
import com.savbill.integrationsystem.deviceveri.mapper.CustomerLedgerMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomerLedgerDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomerLedgerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerLedgerService extends ExBaseAbstractService<CustomerLedgerDTO, CustomerLedgerData, Long> {


    @Autowired
    private CustomerLedgerRepo repo;

    @Autowired
    private CustomerLedgerMapper mapper;

    public CustomerLedgerService(CustomerLedgerRepo repo, CustomerLedgerMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CustomerLedgerService[]";
    }
}
