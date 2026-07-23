package com.savbill.ticketmanagement.core.modules.acl.repository;


import com.savbill.ticketmanagement.core.modules.acl.domain.AclMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AclMenuRepository extends JpaRepository<AclMenu, Long> {
}
