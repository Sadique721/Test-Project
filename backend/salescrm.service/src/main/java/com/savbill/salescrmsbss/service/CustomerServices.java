package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Customers;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateCustomerShareDataMessage;
import com.savbill.salescrmsbss.repository.CustomersRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServices {
    @Autowired
    private CustomersRepository customersRepository;

    private final Logger logger = Logger.getLogger(CustomerServices.class);
    public void updateCustomers(UpdateCustomerShareDataMessage message){
    try {
        Customers customer  = customersRepository.findById(message.getId()).orElse(null);
        // Set values from message to customer object
        if (customer != null) {
            customer.setTitle(message.getTitle());
            customer.setUsername(message.getUsername());
            customer.setPassword(message.getPassword());
            customer.setFirstname(message.getFirstname());
            customer.setLastname(message.getLastname());
            customer.setStatus(message.getStatus());
            customer.setIsDeleted(message.getIsDeleted());
            // Save the customer using the repository
            try {
                customersRepository.save(customer);
            }catch (Exception e){
                e.getMessage();
            }

        }
    }catch (Exception e){
        logger.error("Error while updating Customer,"+e.getMessage());
    }

}
}
