package com.savbill.integrationsystem.CustomerPackage.entity;

import com.savbill.integrationsystem.rabbitmq.CustPlanMappingUpdateMessage;
import com.savbill.integrationsystem.rabbitmq.CustomerPackageRelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerPackageService {

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    public CustomerPackage save(CustomerPackageRelMessage message){
        try {
            if (message.getData() != null) {
                CustomerPackage custPlanMappping = new CustomerPackage(message.getData());
                CustomerPackage mappping = customerPackageRepository.save(custPlanMappping);
                return mappping;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void update(CustPlanMappingUpdateMessage message){
        try {
            if (message!= null && message.getCustomerPlanMappingIds()!=null && message.getDebitDocumentId()!=null) {
                List<Integer> planMappingIds =message.getCustomerPlanMappingIds();
                Integer debitDocId=message.getDebitDocumentId();
                if(planMappingIds!=null && !planMappingIds.isEmpty())
                {
                    planMappingIds.stream().forEach(id->{
                        Optional<CustomerPackage> customerPackage = customerPackageRepository.findById(id.longValue());
                        if(customerPackage.isPresent())
                        {
                            CustomerPackage obj=customerPackage.get();
                            obj.setDebitdocid(debitDocId);
                            customerPackageRepository.save(obj);
                        }
                    });
                }
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
