package com.savbill.notification.repository;

import com.savbill.notification.entity.SmsConfig;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface SmsConfigRepository extends JpaRepository<SmsConfig, Long>,QuerydslPredicateExecutor<SmsConfig>
{
	Optional<SmsConfig> findBySmsConfigIdAndMvnoId(Long smsConfigId, Long mvnoId);
	Optional<SmsConfig> findBySmsConfigIdAndMvnoIdAndBuId(Long smsConfigId, Long mvnoId,Long buId);
	Optional<SmsConfig> findByMvnoId(Long mvnoId);

	Optional<SmsConfig> findByMvnoIdAndBuId(Long mvnoId, Long buId);

	List<SmsConfig> findAllByConfigStatus(Boolean isconfigstatus);

	List<SmsConfig> findAllByMvnoIdAndBuIdAndConfigStatus(Long mvnoId, Long buId,Boolean isconfigstatus);
	Optional<SmsConfig> findByMvnoIdIn(List<Long> mvnoId);

	Optional<SmsConfig> findBySmsConfigId(Long smsConfigId);

    Page<SmsConfig> findAllByServiceTypeContainingIgnoreCase(String serviceType, Pageable pageable);
	Page<SmsConfig> findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(String serviceType, List<Long> mvnoIds,Pageable pageable);
	Page<SmsConfig> findAllByServiceTypeContainingIgnoreCaseAndMvnoIdInAndBuIdIn(String serviceType, List<Long> mvnoIds, List<Long> buIds,Pageable pageable);

	Page<SmsConfig> findAllBySmsUrlIsContainingIgnoreCase(String smsconfigurl, Pageable pageable);
	Page<SmsConfig> findAllBySmsUrlIsContainingIgnoreCaseAndMvnoIdIn(String smsconfigurl, List<Long> mvnoId, Pageable pageable);

	Page<SmsConfig> findAll(Specification<SmsConfig> spec, Pageable pageable);

	List<SmsConfig> findAllBySmsUrlContainingIgnoreCaseAndMvnoId(String smsUrl, Long mvnoId);
}
