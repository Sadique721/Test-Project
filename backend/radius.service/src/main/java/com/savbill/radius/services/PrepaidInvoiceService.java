package com.savbill.radius.services;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.kafka.message.CaftoCustomerMessage;
import com.savbill.radius.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
public class PrepaidInvoiceService {
    @Autowired
    private CustomersRepository customersRepository;
    public void cafToCustomer(CaftoCustomerMessage message) {
        try {
            Customers customersCaf = customersRepository.findById(message.getCustomerId()).orElse(null);
            customersCaf.setCafApproveStatus(message.getCafApproveStatus() != null ? message.getCafApproveStatus() : null);
            customersCaf.setStatus(message.getStatus() != null ? message.getStatus() : null);
            if(customersCaf.getFirstActivationDate()==null) {
                customersCaf.setFirstActivationDate(LocalDateTime.now());
            }
            customersRepository.save(customersCaf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Activate Service In Radius for Service Activation For CAF Workflow Action
     * @param message
     */
    public void cafToCustomerOnServiceActivation(CaftoCustomerMessage message) {
        try {
            Customers customersCaf = customersRepository.findById(message.getCustomerId()).orElse(null);
            customersCaf.setStatus(message.getStatus() != null ? message.getStatus() : null);
            customersCaf.setFirstActivationDate(LocalDateTime.now());
            customersRepository.save(customersCaf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}