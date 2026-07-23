package com.savbill.partnermanagement.modules.acl.repository;

import com.savbill.partnermanagement.modules.acl.domain.CustomACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {
}