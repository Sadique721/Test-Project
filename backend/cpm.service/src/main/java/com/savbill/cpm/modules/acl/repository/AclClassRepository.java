package com.savbill.cpm.modules.acl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.acl.domain.AclClass;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}