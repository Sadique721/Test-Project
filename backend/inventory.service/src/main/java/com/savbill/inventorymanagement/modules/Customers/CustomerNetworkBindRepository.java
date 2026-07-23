package com.savbill.inventorymanagement.modules.Customers;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface CustomerNetworkBindRepository extends JpaRepository<CustomerNetworkBind, Long>, QuerydslPredicateExecutor<CustomerNetworkBind> {

    @Query(value = "SELECT * FROM tbltcustomernetworkbind WHERE customerid = :customerId", nativeQuery = true)
    Optional<CustomerNetworkBind> findByCustomerId(@Param("customerId") Long customerId);
}
