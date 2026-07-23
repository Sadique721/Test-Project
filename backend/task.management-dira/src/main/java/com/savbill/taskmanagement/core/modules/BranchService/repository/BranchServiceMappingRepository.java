package com.savbill.taskmanagement.core.modules.BranchService.repository;

import com.savbill.taskmanagement.core.modules.BranchService.model.BranchServiceMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchServiceMappingRepository extends JpaRepository<BranchServiceMappingEntity , Long> {
    List<BranchServiceMappingEntity> findAllByBranchId(Long branchId);

}
