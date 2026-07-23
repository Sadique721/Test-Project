package com.savbill.partnermanagement.modules.acl.repository;


import com.savbill.partnermanagement.modules.acl.domain.AclClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}