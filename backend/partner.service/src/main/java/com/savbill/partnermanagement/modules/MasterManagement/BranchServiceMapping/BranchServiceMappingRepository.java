package com.savbill.partnermanagement.modules.MasterManagement.BranchServiceMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchServiceMappingRepository extends JpaRepository<BranchServiceMappingEntity , Integer> {
    List<BranchServiceMappingEntity> findAllByBranchId(Long branchId);
}
