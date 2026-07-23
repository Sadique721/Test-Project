package com.savbill.inventorymanagement.modules.acl.repository;

import com.savbill.inventorymanagement.modules.acl.domain.AclClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}