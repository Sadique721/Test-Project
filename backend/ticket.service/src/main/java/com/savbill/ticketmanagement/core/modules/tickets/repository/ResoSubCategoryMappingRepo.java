package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.ResoSubCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResoSubCategoryMappingRepo extends JpaRepository<ResoSubCategoryMapping, Long>, QuerydslPredicateExecutor<ResoSubCategoryMapping> {

    List<ResoSubCategoryMapping> findBysubcateId(Long subCategoryId);
    List<ResoSubCategoryMapping> findAllByResId(Long resId);
}
