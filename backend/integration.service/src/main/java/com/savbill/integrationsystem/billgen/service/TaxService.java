package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.TaxData;
import com.savbill.integrationsystem.billgen.repository.TaxRepository;
import com.savbill.integrationsystem.rabbitmq.TaxMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxService {

    @Autowired
    TaxRepository taxRepository;

    public void save(TaxMessage message) {
        TaxData taxData=new TaxData(message);
        taxRepository.save(taxData);

    }
}
