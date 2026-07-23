package com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserServiceAreaMappingRepository extends JpaRepository<StaffUserServiceAreaMapping, Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {

    List<StaffUserServiceAreaMapping> findByStaffIdIn(List<Integer> staffId);
    List<StaffUserServiceAreaMapping> findByStaffId(Integer staffId);


    List<StaffUserServiceAreaMapping> findAllByServiceId(Integer staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);

    List<StaffUserServiceAreaMapping> findAllByStaffId (Integer staffId);
    @Query(value = "select t.serviceId from StaffUserServiceAreaMapping t where t.staffId=:staffId")
    List<Integer> findServiceAreaByStaffId (Integer staffId);


    List<StaffUserServiceAreaMapping> findAllByStaffIdIn(List<Integer> staffId);

    @Query(value = "select t.staffId from StaffUserServiceAreaMapping t where t.serviceId=:serviceId")
    List<Integer> findStaffIdByServiceAreaId (Integer serviceId);

    @Query("SELECT COUNT(m) > 0 FROM StaffUserServiceAreaMapping m " +
            "WHERE m.staffId = :staffId " +
            "AND m.serviceId IN :serviceAreaIds ")
    boolean existsByStaffIdAndServiceAreaIdIn(
            @Param("staffId") Integer staffId,
            @Param("serviceAreaIds") List<Integer> serviceAreaIds);

}

