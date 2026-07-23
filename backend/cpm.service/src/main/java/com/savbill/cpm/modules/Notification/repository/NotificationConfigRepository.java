package com.savbill.cpm.modules.Notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.Notification.domain.NotificationConfig;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig,Long> {
}
