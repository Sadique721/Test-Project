package com.savbill.inventorymanagement.modules.acl.repository;

import com.savbill.inventorymanagement.modules.acl.domain.CustomACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {
}