package com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.repository;

import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.domain.WorkflowAssignStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowAssignStaffMappingRepo extends JpaRepository<WorkflowAssignStaffMapping, Long>, QuerydslPredicateExecutor<WorkflowAssignStaffMapping> {
    List<WorkflowAssignStaffMapping> findAllByEntityIdAndStaffIdAndTeamHierarchyMappingId(Integer entityId, Integer staffId, Integer teamHierarchyMappingId);
}
