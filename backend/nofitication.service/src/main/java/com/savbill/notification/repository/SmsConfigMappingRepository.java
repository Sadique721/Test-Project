package com.savbill.notification.repository;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.notification.entity.SmsConfigMapping;

@JaversSpringDataAuditable
@Repository
public interface SmsConfigMappingRepository extends JpaRepository<SmsConfigMapping, Long>,QuerydslPredicateExecutor<SmsConfigMapping> 
{
	List<SmsConfigMapping> findSmsConfigMappingBySmsConfigId(Long smsConfigId);
	List<SmsConfigMapping> findBySmsConfigIdAndMvnoId(Long smsConfigId,Long mvnoId);

	Optional<SmsConfigMapping> findBySmsConfigMappingIdAndMvnoId(Long id, Long mvnoId);

	List<SmsConfigMapping> findBySmsConfigId(Long smsConfigId);

	List<SmsConfigMapping> findByMvnoId(Long smsConfigId);
}
