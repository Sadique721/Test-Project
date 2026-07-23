package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.ServiceParameter;

@Repository
public interface ServcieParametersRepository
		extends JpaRepository<ServiceParameter, Long>, QuerydslPredicateExecutor<ServiceParameter> {
	ServiceParameter findAllById(Long Id);
}
