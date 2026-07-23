package com.savbill.cpm.modules.tickets.repository;

import com.savbill.cpm.modules.tickets.domain.ResoSubCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResoSubCategoryMappingRepo extends JpaRepository<ResoSubCategoryMapping, Long>, QuerydslPredicateExecutor<ResoSubCategoryMapping> {

}
