package com.savbill.notification.repository;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.notification.entity.SystemConfig;
@JaversSpringDataAuditable
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long>, QuerydslPredicateExecutor<SystemConfig> {
	Optional<SystemConfig> findByKeyAndServiceName(String key, String serviceName);
}
