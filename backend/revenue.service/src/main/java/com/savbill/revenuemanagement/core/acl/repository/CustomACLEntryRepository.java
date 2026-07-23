package com.savbill.revenuemanagement.core.acl.repository;


import com.savbill.revenuemanagement.core.acl.domain.CustomACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {

//    public List<CustomACLEntry> findAllByRole_IdIn(List<Long> id);
}
