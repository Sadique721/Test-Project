package com.savbill.notification.repository;

import com.savbill.notification.entity.NotificationConfig;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
@JaversSpringDataAuditable
@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long>,QuerydslPredicateExecutor<NotificationConfig>
{
	Optional<NotificationConfig> findByNotificationconfigIdAndMvnoId(Long notificationConfigId, Long mvnoId);
	Optional<NotificationConfig> findByMvnoId(Long mvnoId);

	Optional<NotificationConfig> findByMvnoIdAndBuId(Long mvnoId, Long buId);
	Optional<NotificationConfig> findByMvnoIdIn(List<Long> mvnoId);

	Optional<NotificationConfig> findByBuId(Long buId);
}
