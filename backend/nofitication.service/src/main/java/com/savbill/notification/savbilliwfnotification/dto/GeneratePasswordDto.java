package com.savbill.notification.savbilliwfnotification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratePasswordDto implements CommonNotificationDto {
    private String genPassUrl;
    private String username;
    private String eventName;
    private Long eventId;
    private Map<String, Object> manualMailContent = new HashMap();
    private String applicationName;
    private String email;
}
