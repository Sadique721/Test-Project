package com.savbill.inventorymanagement.modules.MasterManagement.StaffRolRel;

import com.savbill.inventorymanagement.modules.Role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRolRelRepo extends JpaRepository<StaffRoleRel, Long>, QuerydslPredicateExecutor<Role> {
    @Query(value = "select t.roleId from StaffRoleRel t where t.staffId=:staffId")
    List<Long> findRoleIdByStaffId(Long staffId);
}
