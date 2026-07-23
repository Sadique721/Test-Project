package com.savbill.commonGateway.moules.Notification.repository;

import com.savbill.commonGateway.moules.Notification.domain.NotificationRepeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepeatRepository extends JpaRepository<NotificationRepeat, Long> {
    List<NotificationRepeat> findBySubscriberidAndNotificationidAndPackrelid(Long subscriberid, Long packrelid, Long notificationid);
}
