package com.savbill.radius.repository;

import com.savbill.radius.entity.COAResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface COAResponseRepository extends JpaRepository<COAResponse, Integer>, QuerydslPredicateExecutor<COAResponse> {

}
