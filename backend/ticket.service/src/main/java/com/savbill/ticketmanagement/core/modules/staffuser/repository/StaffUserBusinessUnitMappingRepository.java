package com.savbill.ticketmanagement.core.modules.staffuser.repository;

import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUserBusinessUnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserBusinessUnitMappingRepository extends JpaRepository<StaffUserBusinessUnitMapping, Long>, QuerydslPredicateExecutor<StaffUserBusinessUnitMapping> {

//    List<StaffUserBusinessUnitMapping> findByStaffId(List<Integer> staffId);
//
//    @Query(value = "select businessunitid from tblstaffbusinessunitrel t where t.staffid = :staffId", nativeQuery = true)
//    Long findBuidByStaffId(Integer staffId);

    List<StaffUserBusinessUnitMapping> findAllByBusinessunitId(Integer buId);
}
