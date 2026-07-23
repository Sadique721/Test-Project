package com.savbill.partnermanagement.modules.MasterManagement.StaffRolRel;

import com.savbill.partnermanagement.modules.Role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRolRelRepo extends JpaRepository<StaffRoleRel, Long>, QuerydslPredicateExecutor<Role> {
}
