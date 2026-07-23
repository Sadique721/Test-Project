package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Integer> {

    CustomerLedger findByCustomer(Customers customers);
    @Query(value = "select * from savbillrevenuemanagement.TBLMCUSTLEDGER led where led.CUSTID =:id",nativeQuery = true)
    Optional<CustomerLedger> findByCustomerId(@Param("id") Integer id);
}
