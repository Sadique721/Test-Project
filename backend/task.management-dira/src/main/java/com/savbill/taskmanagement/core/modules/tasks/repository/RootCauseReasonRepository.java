package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository
public interface RootCauseReasonRepository extends JpaRepository<RootCauseResolutionMapping, Long> {
}
