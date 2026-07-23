package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustomerAddress;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
    CustomerAddress findByAddressTypeAndCustomerAndVersion(String addressType, Customers customer, String version);
        List<CustomerAddress> findAddressesByCustomerId(Integer customerId);
        boolean existsById(Integer customerId);

    @Query("SELECT MAX(c.id) FROM CustomerAddress c")
    Integer findMaxId();

    List<CustomerAddress> findAllByCustomer_Id(Integer custId);

}