package com.savbill.ticketmanagement.core.modules.TicketFollowupDetail.repository;

import com.savbill.ticketmanagement.core.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketFollowupDetailRepository extends JpaRepository<TicketFollowupDetail, Long> {

	List<TicketFollowupDetail> getAllByCaseId(Long caseId);

}
