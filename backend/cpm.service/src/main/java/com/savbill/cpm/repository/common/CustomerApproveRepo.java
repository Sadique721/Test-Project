package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.CustomerApprove;
import com.savbill.cpm.model.common.Customers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CustomerApproveRepo extends JpaRepository<CustomerApprove, Integer>, QuerydslPredicateExecutor<CustomerApprove> {

    Customers findBycustName(String custName);

    CustomerApprove findByCustomerID(Integer custId);

    CustomerApprove findByCustomerIDAndStatus(Integer customerID, String status);

    List<CustomerApprove> findAllByCustomerIDIsAndCurrentStaffIsNotNullAndParentStaffIsNotNullAndStatus(Integer custId,String status);

    List<CustomerApprove>findAllByCustomerIDIsAndCurrentStaffIsNotNullAndStatusEquals(Integer custId,String status);
}
