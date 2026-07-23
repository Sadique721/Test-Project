package com.savbill.notification.savbilliwfnotification.dto;

import com.savbill.notification.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMaster {

    private String actionDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private Map<String, Object> mailContent = new ConcurrentHashMap<>();
    private Map<String, Object> manualMailContent = new ConcurrentHashMap<>();
    private String isSmsConfigured;
    private String isEmailConfigured;
    private String status;
    private String eventName;
    private Long eventId;
    private String subject;
    private EventTemplateBindingDTO eventTemplateBindingDTO;
    private Event event;
    private String appName;
}
