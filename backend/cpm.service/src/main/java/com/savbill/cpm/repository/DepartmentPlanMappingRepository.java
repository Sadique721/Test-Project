package com.savbill.cpm.repository;

import com.savbill.cpm.model.postpaid.DepartmentPlanMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentPlanMappingRepository extends JpaRepository<DepartmentPlanMapping, Integer> {
    @Query("SELECT DISTINCT dpm.planId.id FROM DepartmentPlanMapping dpm WHERE dpm.department.id = :departmentId")
    List<Integer> findDistinctPlanIdsByDepartmentId(@Param("departmentId") Integer departmentId);
}