package com.savbill.partnermanagement.modules.MasterManagement.StaffServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserServiceAreaMappingRepository extends JpaRepository<StaffUserServiceAreaMapping, Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {
    List<StaffUserServiceAreaMapping>findAllByStaffIdIn(List<Integer> staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceId(Integer staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);

    List<StaffUserServiceAreaMapping> findAllByStaffId (Integer staffId);


}
