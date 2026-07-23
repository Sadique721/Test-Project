package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.PlanServiceData;
import com.savbill.integrationsystem.billgen.repository.PlangroupRepocitory;
import com.savbill.integrationsystem.rabbitmq.PlanServiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanGroupService {
    @Autowired
    PlangroupRepocitory plangroupRepocitory;
    public void save(PlanServiceMessage message) {
        PlanServiceData planServiceData = new PlanServiceData(message);
        //ChargeData chargeData=new ChargeData(message);
        plangroupRepocitory.save(planServiceData);
    }
}
