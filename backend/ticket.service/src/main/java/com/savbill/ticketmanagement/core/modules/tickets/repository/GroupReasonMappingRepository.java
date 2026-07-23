package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryGroupReasonMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupReasonMappingRepository extends JpaRepository<TicketSubCategoryGroupReasonMapping, Long> {
}
