package com.savbill.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.AuthResponse;

@Repository
public interface AuthResponseRepository extends JpaRepository<AuthResponse, Long>,QuerydslPredicateExecutor<AuthResponse>{
	
	List<AuthResponse> findByUserName(String userName);

	List<AuthResponse> findByUserNameContaining(String userName);
}
