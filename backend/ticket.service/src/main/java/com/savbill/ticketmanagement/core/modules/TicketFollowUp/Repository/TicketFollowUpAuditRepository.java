package com.savbill.ticketmanagement.core.modules.TicketFollowUp.Repository;


import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUpAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketFollowUpAuditRepository extends JpaRepository<TicketFollowUpAudit, Long> {
}
