package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository
public interface RootCauseReasonRepository extends JpaRepository<RootCauseResolutionMapping, Long> {
}
