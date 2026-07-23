package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.BusinessUnit;
import com.savbill.integrationsystem.billgen.repository.BusinessUnitRepo;
import com.savbill.integrationsystem.rabbitmq.BusinessUnitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessUnitService {

    @Autowired
    BusinessUnitRepo businessUnitRepo;

    public void save(BusinessUnitMessage message){
        BusinessUnit businessUnit = new BusinessUnit(message);
        businessUnitRepo.save(businessUnit);
    }
}
