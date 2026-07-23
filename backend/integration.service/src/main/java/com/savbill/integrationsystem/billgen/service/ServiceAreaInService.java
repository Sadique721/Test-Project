package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.ServiceArea;
import com.savbill.integrationsystem.billgen.repository.ServiceAreaInRepo;
import com.savbill.integrationsystem.rabbitmq.ServiceAreaIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceAreaInService {

    @Autowired
    ServiceAreaInRepo serviceAreaInRepo;

    public void save(ServiceAreaIn message){
        ServiceArea serviceArea = new ServiceArea(message);
        serviceAreaInRepo.save(serviceArea);


    }


}
