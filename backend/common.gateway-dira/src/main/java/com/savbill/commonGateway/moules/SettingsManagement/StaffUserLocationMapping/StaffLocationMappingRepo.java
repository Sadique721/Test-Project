package com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffLocationMappingRepo extends JpaRepository<StaffUserLocationMapping, Long>, QuerydslPredicateExecutor<StaffUserLocationMapping> {
    List<StaffUserLocationMapping> findAllByStaffId(Long staffId);
    List<StaffUserLocationMapping> findAllByLocationIdIn(List<Long> locationIds);
}
