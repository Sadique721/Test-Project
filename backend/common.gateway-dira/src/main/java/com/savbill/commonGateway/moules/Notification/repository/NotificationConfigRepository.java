package com.savbill.commonGateway.moules.Notification.repository;

import com.savbill.commonGateway.moules.Notification.domain.NotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig,Long> {
}
