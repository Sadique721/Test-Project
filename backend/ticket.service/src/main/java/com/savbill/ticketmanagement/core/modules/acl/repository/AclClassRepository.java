package com.savbill.ticketmanagement.core.modules.acl.repository;

import com.savbill.ticketmanagement.core.modules.acl.domain.AclClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}
