package com.savbill.integrationsystem.Services;

import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResetUsageForAccountService {
    @Autowired
    private CustomerRepository customerRepository;

   /* @Autowired
    private RadiusClient radiusClient;*/
    public void resetUsageForAccount(Integer id) {
        CustomerData customerData = customerRepository.getOne(id);

    }
}
