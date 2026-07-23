package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersDataRepository extends JpaRepository<CustomerData, Integer> {
    CustomerData findCustomerDataByUsername(String userName);
}
