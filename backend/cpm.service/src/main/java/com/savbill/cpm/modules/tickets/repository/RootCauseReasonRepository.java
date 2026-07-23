package com.savbill.cpm.modules.tickets.repository;

import com.savbill.cpm.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootCauseReasonRepository extends JpaRepository<RootCauseResolutionMapping, Long> {
}
