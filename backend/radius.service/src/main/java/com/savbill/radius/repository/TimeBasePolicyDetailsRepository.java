package com.savbill.radius.repository;

import com.savbill.radius.entity.TimeBasePolicyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeBasePolicyDetailsRepository extends JpaRepository<TimeBasePolicyDetails , Long> {
}
