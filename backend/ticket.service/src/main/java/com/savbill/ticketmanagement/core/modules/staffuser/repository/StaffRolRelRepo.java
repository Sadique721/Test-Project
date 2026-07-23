package com.savbill.ticketmanagement.core.modules.staffuser.repository;


import com.savbill.ticketmanagement.core.modules.role.domain.Role;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffRoleRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRolRelRepo extends JpaRepository<StaffRoleRel, Long>, QuerydslPredicateExecutor<Role> {

    List<StaffRoleRel> findByRoleId(Long roleId);

}
