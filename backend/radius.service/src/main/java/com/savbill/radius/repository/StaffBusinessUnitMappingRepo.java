package com.savbill.radius.repository;

import com.savbill.radius.entity.StaffUserBusinessUnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffBusinessUnitMappingRepo extends JpaRepository<StaffUserBusinessUnitMapping,Long>, QuerydslPredicateExecutor<StaffUserBusinessUnitMapping> {
    List<StaffUserBusinessUnitMapping> findAllByStaffId(Integer staffId);
}
