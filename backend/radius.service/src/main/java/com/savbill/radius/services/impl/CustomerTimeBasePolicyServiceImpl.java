package com.savbill.radius.services.impl;

import com.savbill.radius.entity.CustomerTimeBasePolicyDetails;
import com.savbill.radius.kafka.message.CustomerTimeBasePolicyDetailsMessage;
import com.savbill.radius.repository.CustomerTimeBasePolicyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerTimeBasePolicyServiceImpl {
    @Autowired
    private CustomerTimeBasePolicyDetailsRepository customerTimeBasePolicyDetailsRepository;

    public CustomerTimeBasePolicyDetails save(CustomerTimeBasePolicyDetailsMessage message){
        try {
            if (message.getData() != null) {
                CustomerTimeBasePolicyDetails customerTimeBasePolicyDetails = new CustomerTimeBasePolicyDetails(message);
                CustomerTimeBasePolicyDetails customerTimeBasePolicyDetailsSave = customerTimeBasePolicyDetailsRepository.save(customerTimeBasePolicyDetails);
                return customerTimeBasePolicyDetailsSave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
