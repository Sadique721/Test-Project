package com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserBusinessUnitMappingRepository extends JpaRepository<StaffUserBusinessUnitMapping, Long>, QuerydslPredicateExecutor<StaffUserBusinessUnitMapping> {



    @Query(value = "select t.businessunitId from StaffUserBusinessUnitMapping t where t.staffId=:staffId")
    List<Long> findBuidByStaffId(Integer staffId);

    List<StaffUserBusinessUnitMapping> findAllByBusinessunitIdIn(List<Long> businessunitId);

    @Query("SELECT su.businessunitId FROM StaffUserBusinessUnitMapping su WHERE su.staffId = :staffId")
    List<Long> findBusinessUnitIdsByStaffId(@Param("staffId") Integer staffId);

}
