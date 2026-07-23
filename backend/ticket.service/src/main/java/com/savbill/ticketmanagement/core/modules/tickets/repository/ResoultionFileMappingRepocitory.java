package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.ResoultionFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResoultionFileMappingRepocitory extends JpaRepository<ResoultionFileMapping,Long> , QuerydslPredicateExecutor<ResoultionFileMapping> {
    @Query("SELECT t FROM ResoultionFileMapping t WHERE t.resolution.id = :resolutionId and t.caseId is not null ")
    List<ResoultionFileMapping> findByResolutionId(@Param("resolutionId") Long resolutionId);

}
