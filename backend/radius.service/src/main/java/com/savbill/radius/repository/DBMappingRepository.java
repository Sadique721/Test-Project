package com.savbill.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.DBMapping;

@Repository
public interface DBMappingRepository extends JpaRepository<DBMapping, Long>, QuerydslPredicateExecutor<DBMapping> {

	List<DBMapping> findDBMappingByMappingMasterId(Long dbMappingMasterId);

}
