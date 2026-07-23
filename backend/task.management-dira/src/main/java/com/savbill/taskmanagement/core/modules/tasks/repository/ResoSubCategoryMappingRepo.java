package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.ResoSubCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResoSubCategoryMappingRepo extends JpaRepository<ResoSubCategoryMapping, Long>, QuerydslPredicateExecutor<ResoSubCategoryMapping> {

    //List<ResoSubCategoryMapping> findBysubcateId(Long subCategoryId);

    List<ResoSubCategoryMapping> findByCaseCategoryId(Integer caseCategoryId);
    List<ResoSubCategoryMapping> findAllByResId(Long resId);
}
