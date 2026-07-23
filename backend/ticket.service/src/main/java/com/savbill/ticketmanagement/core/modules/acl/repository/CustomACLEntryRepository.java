package com.savbill.ticketmanagement.core.modules.acl.repository;


import com.savbill.ticketmanagement.core.modules.acl.domain.CustomACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {

    public List<CustomACLEntry> findAllByRoleid_IdIn(List<Long> id);
}
