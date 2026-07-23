package com.savbill.commonGateway.moules.MasterManagement.BranchService.repository;


import com.savbill.commonGateway.moules.MasterManagement.BranchService.model.BranchServiceMappingEntity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@JaversSpringDataAuditable
public interface BranchServiceMappingRepository extends JpaRepository<BranchServiceMappingEntity, Long> {
    List<BranchServiceMappingEntity> findAllByBranchId(Long branchId);

}
