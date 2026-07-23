package com.savbill.notification.services;

import com.savbill.notification.entity.NotificationConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NotificationConfigService
{
	NotificationConfig updateNotificationConfig(NotificationConfig notificationConfig, Long mvnoId, HttpServletRequest request);
	List<NotificationConfig> findAllSmsConfig(Long mvnoId, Long buId);
	NotificationConfig addNotificationConfig(String smsUrl, Long mvnoId, String createdBy , Long buId);
	NotificationConfig findNotificationConfigById(Long notificationConfigId, Long mvnoId);
}
