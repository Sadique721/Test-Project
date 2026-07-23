package com.savbill.inventorymanagement.modules.InvoiceService;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.CaftoCustomerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
@Service
public class PrepaidInvoiceService {
    @Autowired
    private CustomersRepository customersRepository;

    private static final Logger logger = Logger.getLogger(PrepaidInvoiceService.class);

    public void cafToCustomer(CaftoCustomerMessage message) {
        try {
            Customers customersCaf = customersRepository.findById(message.getCustomerId()).orElse(null);
            customersCaf.setCafApproveStatus(message.getCafApproveStatus() != null ? message.getCafApproveStatus() : null);
            customersCaf.setStatus(message.getStatus() != null ? message.getStatus() : null);
            customersCaf.setFirstActivationDate(LocalDateTime.now());
            customersRepository.save(customersCaf);
            logger.info("Update customer successfully with customer id: " + message.getCustomerId());
        } catch (CustomValidationException e) {
            logger.error("Unable to update customer successfully with customer id: " + message.getCustomerId() + " , Error: " + e.getMessage());
        }
    }

}
