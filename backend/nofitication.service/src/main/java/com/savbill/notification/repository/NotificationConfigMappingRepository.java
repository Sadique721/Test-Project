package com.savbill.notification.repository;


import com.savbill.notification.entity.NotificationConfigMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface NotificationConfigMappingRepository extends JpaRepository<NotificationConfigMapping, Long>,QuerydslPredicateExecutor<NotificationConfigMapping>
{
	//List<NotificationConfigMapping> findNotificationConfigMappingBySmsConfigId(Long smsConfigId);
	List<NotificationConfigMapping> findByNotificationconfigIdAndMvnoId(Long smsConfigId,Long mvnoId);

	List<NotificationConfigMapping> findAllByNotificationconfigIdAndParameterContainingIgnoreCase(Long smsConfigId , String parameter);

	//Optional<NotificationConfigMapping> findByNotificationConfigMappingIdAndMvnoId(Long id, Long mvnoId);
}
