package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
}
