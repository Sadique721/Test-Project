package com.savbill.taskmanagement.core.modules.Plan.repository;


import com.savbill.taskmanagement.core.modules.Plan.domain.CustPlanMappping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustPlanMappingRepository extends JpaRepository<CustPlanMappping, Long>, QuerydslPredicateExecutor<CustPlanMappping> {

    CustPlanMappping findById(Integer id);
}

