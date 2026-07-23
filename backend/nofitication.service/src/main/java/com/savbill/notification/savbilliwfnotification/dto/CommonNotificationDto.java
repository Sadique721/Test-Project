package com.savbill.notification.savbilliwfnotification.dto;

import java.util.Map;

public interface CommonNotificationDto {
    String getEventName();

    Long getEventId();

    String getApplicationName();

    Map<String, Object> getManualMailContent();
}
