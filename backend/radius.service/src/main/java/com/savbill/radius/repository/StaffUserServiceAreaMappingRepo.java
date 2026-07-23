package com.savbill.radius.repository;

import com.savbill.radius.entity.StaffUserServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserServiceAreaMappingRepo extends JpaRepository<StaffUserServiceAreaMapping,Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {
    List<StaffUserServiceAreaMapping> findAllByStaffId(Integer staffId);
}
