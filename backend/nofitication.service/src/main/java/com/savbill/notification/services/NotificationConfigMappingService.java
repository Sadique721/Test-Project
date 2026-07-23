package com.savbill.notification.services;

import com.savbill.notification.entity.NotificationConfigMapping;
import com.savbill.notification.helper.NotificationConfigMappingDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface NotificationConfigMappingService
{
    List<NotificationConfigMapping> findNotificationConfigMappingBySmsConfigId(Long notificationConfigId, Long mvnoId);
    List<NotificationConfigMapping> findAllNotificationConfigMapping(Long mvnoId);
    void deleteNotificationConfigMappingById(Long id,Long mvnoId);
    List<NotificationConfigMapping> saveNotificationConfigMapping(List<NotificationConfigMappingDto> notficationConfigMappingDtoList, Long mvnoId);
    List<NotificationConfigMapping> updateNotificationConfigMapping(List<NotificationConfigMappingDto> notificationConfigMappingDtoList, Long mvnoId, Long smsConfigId, HttpServletRequest request);
}
