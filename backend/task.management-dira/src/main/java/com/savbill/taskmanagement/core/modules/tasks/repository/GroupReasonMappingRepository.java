package com.savbill.taskmanagement.core.modules.tasks.repository;


import com.savbill.taskmanagement.core.modules.tasks.domain.TicketSubCategoryGroupReasonMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupReasonMappingRepository extends JpaRepository<TicketSubCategoryGroupReasonMapping, Long> {
}
