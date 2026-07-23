package com.savbill.partnermanagement.modules.MasterManagement.Pincode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, Long>, QuerydslPredicateExecutor<Pincode>  {
}
