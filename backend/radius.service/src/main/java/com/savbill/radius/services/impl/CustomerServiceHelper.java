package com.savbill.radius.services.impl;

import com.savbill.radius.entity.CustPlanMappping;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceHelper {

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CustPlanMappping saveCustPlanMappingForceFully(CustPlanMappping custPlanMappping) {
        return custPlanMappingRepository.save(custPlanMappping);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Customers saveCustomer(Customers customers) {
        return customersRepository.save(customers);
    }
}
