package com.savbill.commonGateway.moules.acl.repository;

import com.savbill.commonGateway.moules.acl.domain.CustomACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {

    public List<CustomACLEntry> findAllByRole_IdIn(List<Long> id);
}
