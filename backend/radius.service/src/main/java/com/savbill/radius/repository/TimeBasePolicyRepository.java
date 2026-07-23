package com.savbill.radius.repository;

import com.savbill.radius.entity.TimeBasePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeBasePolicyRepository  extends JpaRepository<TimeBasePolicy , Long> {
}
