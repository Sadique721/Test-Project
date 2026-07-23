package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.DBMappingMaster;

@Repository
public interface DBMappingMasterRepository extends JpaRepository<DBMappingMaster, Long>,QuerydslPredicateExecutor<DBMappingMaster>{

	Optional<DBMappingMaster> findByMappingMasterId(Long id);

	List<DBMappingMaster> findByName(String name);

}
