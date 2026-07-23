package com.savbill.taskmanagement.core.modules.Notification.repository;


import com.savbill.taskmanagement.core.modules.Notification.domain.NotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig,Long> {
}
