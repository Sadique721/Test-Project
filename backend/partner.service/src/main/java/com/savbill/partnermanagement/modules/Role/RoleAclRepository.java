package com.savbill.partnermanagement.modules.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAclRepository extends JpaRepository<RoleACLEntry,Long> {
    @Query("select rce from RoleACLEntry rce where rce.role.id = :id")
    List<RoleACLEntry> findAllByRole( @Param("id") Long id);
}
