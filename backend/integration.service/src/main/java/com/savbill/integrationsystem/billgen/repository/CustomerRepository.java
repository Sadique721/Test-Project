package com.savbill.integrationsystem.billgen.repository;


import com.savbill.integrationsystem.billgen.entity.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerData, Integer> {

    CustomerData findByUsername(String userName);
}

