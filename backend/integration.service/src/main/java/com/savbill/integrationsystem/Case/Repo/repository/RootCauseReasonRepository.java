package com.savbill.integrationsystem.Case.Repo.repository;

import com.savbill.integrationsystem.Case.RootCauseResolutionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootCauseReasonRepository extends JpaRepository<RootCauseResolutionMapping, Long> {
}
