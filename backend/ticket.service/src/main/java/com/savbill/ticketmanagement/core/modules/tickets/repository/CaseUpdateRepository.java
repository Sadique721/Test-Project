package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseUpdateRepository extends JpaRepository<CaseUpdate, Long> {
}
