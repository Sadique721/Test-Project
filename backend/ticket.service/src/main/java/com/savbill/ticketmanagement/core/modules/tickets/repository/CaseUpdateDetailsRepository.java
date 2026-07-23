package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseUpdateDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseUpdateDetailsRepository extends JpaRepository<CaseUpdateDetails, Long> {
}
