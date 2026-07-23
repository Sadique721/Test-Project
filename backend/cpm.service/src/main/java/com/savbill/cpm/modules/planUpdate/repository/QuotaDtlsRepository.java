package com.savbill.cpm.modules.planUpdate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.planUpdate.domain.QuotaDtls;

import java.util.List;

@Repository
public interface QuotaDtlsRepository extends JpaRepository<QuotaDtls, Long>  ,  QuerydslPredicateExecutor<QuotaDtls> {
    public List<QuotaDtls> findAllByCustomersId(Integer id);

}
