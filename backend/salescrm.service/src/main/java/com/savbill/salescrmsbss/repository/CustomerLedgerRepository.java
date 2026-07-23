package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustomerLedger;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Integer>{

}
