package com.savbill.radius.repository;

import com.savbill.radius.entity.AuthModeAttributeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthModeAttributeMappingRepository extends JpaRepository<AuthModeAttributeMapping, Long>, QuerydslPredicateExecutor<AuthModeAttributeMapping> {
}
