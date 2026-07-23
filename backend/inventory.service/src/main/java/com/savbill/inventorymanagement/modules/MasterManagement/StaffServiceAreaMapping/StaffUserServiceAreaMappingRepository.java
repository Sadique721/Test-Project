package com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserServiceAreaMappingRepository extends JpaRepository<StaffUserServiceAreaMapping, Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {
    List<StaffUserServiceAreaMapping> findAllByStaffIdIn(List<Integer> staffId);

    @Query("SELECT s.serviceId FROM StaffUserServiceAreaMapping s WHERE s.staffId = :staffId")
    List<Integer> findServiceIdsByStaffId(@Param("staffId") Integer staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceId(Integer staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);

    @Query("SELECT s.staffId FROM StaffUserServiceAreaMapping s WHERE s.serviceId IN :serviceAreaIds")
    List<Integer> findStaffIdsByServiceIdIn(@Param("serviceAreaIds") List<Integer> serviceAreaIds);

    List<StaffUserServiceAreaMapping> findAllByStaffId (Integer staffId);


}
