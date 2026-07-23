package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.ChargeData;
import com.savbill.integrationsystem.billgen.repository.ChargeRepocitory;
import com.savbill.integrationsystem.rabbitmq.ChargeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ChargeServices {

    @Autowired
    ChargeRepocitory chargeRepocitory;
    public void save(ChargeMessage message) {
        ChargeData chargeData=new ChargeData(message);
        chargeRepocitory.save(chargeData);

    }
}
