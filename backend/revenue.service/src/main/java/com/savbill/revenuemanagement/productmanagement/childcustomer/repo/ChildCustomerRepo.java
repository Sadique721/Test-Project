package com.savbill.revenuemanagement.productmanagement.childcustomer.repo;

import com.savbill.revenuemanagement.productmanagement.childcustomer.entity.ChildCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildCustomerRepo extends JpaRepository<ChildCustomer,Long> {
    ChildCustomer findByIdAndMvnoId(Long id , Long mvnoId);
}
