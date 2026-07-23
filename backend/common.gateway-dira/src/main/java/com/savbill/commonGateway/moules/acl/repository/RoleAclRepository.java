package com.savbill.commonGateway.moules.acl.repository;

import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface RoleAclRepository extends JpaRepository<RoleACLEntry,Long> {
    @Query("select rce from RoleACLEntry rce where rce.role.id = :id")
    List<RoleACLEntry> findAllByRole( @Param("id") Long id);


    @Query("select new com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO(rce.id, rce.code, rce.menuid) from RoleACLEntry rce where rce.role.id = :id")
    List<RoleACLEntryDTO> findAClByRole(@Param("id") Long id);

     @Query("select rce from RoleACLEntry rce where rce.role.id = :id AND rce.product = :product")
    List<RoleACLEntry> findAllByRoleandProduct( @Param("id") Long id,@Param("product")String product);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoleACLEntry r WHERE r.role.id = :roleId AND r.product = :product")
    void deleteByRoleAndProduct(@Param("roleId") Long roleId, @Param("product") String product);


     @Query("select rce.menuid from RoleACLEntry rce where rce.role.id = :id")
     List<Integer> findMenoIdFromRole( @Param("id") Long id);
}
