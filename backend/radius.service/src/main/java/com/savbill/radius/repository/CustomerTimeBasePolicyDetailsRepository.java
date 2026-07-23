package com.savbill.radius.repository;

import com.savbill.radius.entity.CustomerTimeBasePolicyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerTimeBasePolicyDetailsRepository extends JpaRepository<CustomerTimeBasePolicyDetails, Long> {

}
