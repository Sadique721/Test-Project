package com.savbill.cpm.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.tickets.domain.CaseUpdate;

public interface CaseUpdateRepository extends JpaRepository<CaseUpdate, Long> {
}
