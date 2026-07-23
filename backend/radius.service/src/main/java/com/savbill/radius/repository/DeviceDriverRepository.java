package com.savbill.radius.repository;

import com.savbill.radius.entity.DeviceDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDriverRepository extends JpaRepository<DeviceDriver , Long> , QuerydslPredicateExecutor<DeviceDriver> {
}
