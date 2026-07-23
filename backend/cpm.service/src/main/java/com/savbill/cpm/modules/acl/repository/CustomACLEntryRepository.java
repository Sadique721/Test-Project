package com.savbill.cpm.modules.acl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.acl.domain.CustomACLEntry;

public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {

//    public List<CustomACLEntry> findAllByRole_IdIn(List<Long> id);
}
