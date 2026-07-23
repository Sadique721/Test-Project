package com.savbill.radius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.LocationMaster;

@Repository
public interface LocationMasterRepository extends JpaRepository<LocationMaster, Long>,QuerydslPredicateExecutor<LocationMaster> {

	Optional<LocationMaster> findByLocationMasterIdAndMvnoId(Long locationMasterId, Integer mvnoId);
	
}
