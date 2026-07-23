package com.savbill.revenuemanagement.mastermanagement.Department.Repocitory;

import com.savbill.revenuemanagement.mastermanagement.Department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepocitory extends JpaRepository<Department,Integer> {
}
