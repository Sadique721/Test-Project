package com.savbill.integrationsystem.apiAudits.repository;

import com.savbill.integrationsystem.apiAudits.entity.ApiAudits;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiAuditsRepository extends JpaRepository<ApiAudits, Long>, QuerydslPredicateExecutor<ApiAudits> {





    Page<ApiAudits> findAllByMvnoIdIn(List<Long> mvnoId, Pageable pageable);
}
