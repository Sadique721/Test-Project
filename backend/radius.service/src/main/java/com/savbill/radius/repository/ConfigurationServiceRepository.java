package com.savbill.radius.repository;

import com.savbill.radius.entity.ConfigurationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationServiceRepository extends JpaRepository<ConfigurationService, Long>, QuerydslPredicateExecutor<ConfigurationService> {

    Optional<ConfigurationService> findByNameAndMvnoId(String name, Integer mvnoId);
}
