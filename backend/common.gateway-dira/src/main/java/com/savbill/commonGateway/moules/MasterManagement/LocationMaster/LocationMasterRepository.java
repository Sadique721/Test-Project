package com.savbill.commonGateway.moules.MasterManagement.LocationMaster;

import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LocationMaster;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
@Repository
public interface LocationMasterRepository extends JpaRepository<LocationMaster, Long>, QuerydslPredicateExecutor<LocationMaster> {
    Optional<LocationMaster> findAllByLocationMasterIdAndName(Long locationMasterId, String name);
}
