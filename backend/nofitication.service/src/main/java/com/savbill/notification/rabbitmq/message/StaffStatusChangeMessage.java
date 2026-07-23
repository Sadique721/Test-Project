package com.savbill.notification.rabbitmq.message;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class StaffStatusChangeMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String customerName;


    private Map<String, Object> staffUserData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;


}
