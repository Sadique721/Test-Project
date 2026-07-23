package com.savbill.taskmanagement.core.modules.CustomerAddress.repository;



import com.savbill.taskmanagement.core.modules.CustomerAddress.domain.CustomerAddress;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> , QuerydslPredicateExecutor<CustomerAddress> {

    Page<CustomerAddress> findByCustomer(Customers cust, Pageable pageable);

    List<CustomerAddress> findAllByCustomer(Customers customer);

    CustomerAddress findByAddressTypeAndCustomerAndVersion(String addressType, Customers customer , String version);
    CustomerAddress findByAddressTypeAndCustomerIdAndVersion(String addressType, Integer customerId , String version);

    @Query("select t from CustomerAddress t where t.isDelete=false")
    List<CustomerAddress> findAll();

    @Query("update CustomerAddress t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<CustomerAddress> findAllByCustomerAndStatus(Customers customers, String status);

    CustomerAddress findByAddressTypeAndCustomerId(String addressType, Integer customerId );
}
