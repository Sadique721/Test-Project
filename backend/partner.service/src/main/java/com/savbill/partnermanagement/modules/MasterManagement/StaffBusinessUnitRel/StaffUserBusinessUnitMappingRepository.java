package com.savbill.partnermanagement.modules.MasterManagement.StaffBusinessUnitRel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffUserBusinessUnitMappingRepository extends JpaRepository<StaffUserBusinessUnitMapping, Long>, QuerydslPredicateExecutor<StaffUserBusinessUnitMapping> {

}
