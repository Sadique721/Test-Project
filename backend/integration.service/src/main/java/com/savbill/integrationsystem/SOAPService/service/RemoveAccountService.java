package com.savbill.integrationsystem.SOAPService.service;

import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoveAccountService {
    @Autowired
    private CustomerRepository customerRepository;

    public void removeAccount(Integer custId) {
        CustomerData customerData = customerRepository.getOne(custId);
        customerRepository.delete(customerData);  // Assuming CustomerRepository is a Spring Data JPA Repository for Customer entity
    }
}
