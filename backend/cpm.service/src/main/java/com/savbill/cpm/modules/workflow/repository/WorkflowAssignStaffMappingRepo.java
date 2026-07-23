package com.savbill.cpm.modules.workflow.repository;

import com.savbill.cpm.modules.workflow.domain.WorkflowAssignStaffMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface WorkflowAssignStaffMappingRepo extends JpaRepository<WorkflowAssignStaffMapping, Long>, QuerydslPredicateExecutor<WorkflowAssignStaffMapping> {

    WorkflowAssignStaffMapping findByEventNameAndStaffIdAndEntityId(String eventName, Integer staffId, Integer entityId);

    @Modifying
    void deleteAllByEventNameAndEntityId(String eventName, Integer entityId);
    List<WorkflowAssignStaffMapping>findByEventNameAndStaffId(String eventName, Integer staffId);

    List<WorkflowAssignStaffMapping> findAllByEventNameAndEntityId(String eventName, Integer entityId);

}
