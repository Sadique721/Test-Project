package com.savbill.revenuemanagement.core.acl.repository;


import com.savbill.revenuemanagement.core.acl.domain.AclClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}
