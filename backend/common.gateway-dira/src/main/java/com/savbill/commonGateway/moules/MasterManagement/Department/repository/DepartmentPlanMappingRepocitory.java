package com.savbill.commonGateway.moules.MasterManagement.Department.repository;

import com.savbill.commonGateway.moules.MasterManagement.Department.domain.DepartmentPlanMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DepartmentPlanMappingRepocitory extends JpaRepository<DepartmentPlanMapping ,Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from DepartmentPlanMapping p where p.department.id = :id")
    void deleteAllByDepartment_Id(Integer id);
}
