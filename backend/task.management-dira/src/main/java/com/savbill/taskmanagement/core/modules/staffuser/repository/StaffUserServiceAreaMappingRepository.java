package com.savbill.taskmanagement.core.modules.staffuser.repository;


import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUserServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StaffUserServiceAreaMappingRepository extends JpaRepository<StaffUserServiceAreaMapping, Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {

//    List<StaffUserServiceAreaMapping> findByStaffId(List<Integer> staffId);
//
//    List<StaffUserServiceAreaMapping>findAllByStaffId(List<Integer> staffId);
//
//    List<StaffUserServiceAreaMapping> findAllByServiceId(Integer staffId);
//
    List<StaffUserServiceAreaMapping> findAllByStaffId(Integer staffId);
//
//    List<StaffUserServiceAreaMapping> findAllByStaffId (Integer staffId);




}
