package com.savbill.commonGateway.moules.SettingsManagement.StaffUserPlanServiceMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffUserPlanServiceMappingRepository extends JpaRepository<StaffUserPlanServiceMapping,Long>, QuerydslPredicateExecutor<StaffUserPlanServiceMapping> {
}
