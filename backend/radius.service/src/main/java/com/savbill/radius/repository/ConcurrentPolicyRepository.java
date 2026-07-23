package com.savbill.radius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.ConcurrentPolicy;


@Repository
public interface ConcurrentPolicyRepository extends JpaRepository<ConcurrentPolicy, Long>,QuerydslPredicateExecutor<ConcurrentPolicy>{

	Optional<ConcurrentPolicy> searchByName(String name);

	

}
