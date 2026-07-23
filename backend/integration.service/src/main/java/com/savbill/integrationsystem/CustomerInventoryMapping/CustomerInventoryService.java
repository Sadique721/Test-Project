package com.savbill.integrationsystem.CustomerInventoryMapping;

import com.savbill.integrationsystem.rabbitmq.CustomerInventoryMappingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerInventoryService {
    @Autowired
    CustomerInvenrotyMappingRepo customerInvenrotyMappingRepo;

    public CustomerInventoryMappingEntity save(CustomerInventoryMappingMessage message) {
        try {
            if (message.getCustomerInventoryData() != null) {
                CustomerInventoryMappingEntity customerInventoryMappingEntity = new CustomerInventoryMappingEntity(message.getCustomerInventoryData());
                CustomerInventoryMappingEntity mappping = customerInvenrotyMappingRepo.save(customerInventoryMappingEntity);
                 return mappping;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
