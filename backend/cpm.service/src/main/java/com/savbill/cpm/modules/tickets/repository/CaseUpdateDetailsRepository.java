package com.savbill.cpm.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.tickets.domain.CaseUpdateDetails;

public interface CaseUpdateDetailsRepository extends JpaRepository<CaseUpdateDetails, Long> {
}
