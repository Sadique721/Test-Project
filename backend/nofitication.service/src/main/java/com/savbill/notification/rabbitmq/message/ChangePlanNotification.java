package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePlanNotification {
    private  String oldPlanName;
    private String newPlanName;
    private Integer validity;
    private String expiryDate;
    private String username;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String messageId;
    private boolean isEmailConfigured;
    private boolean isSmsConfigured;
    private Map<String, Object> customerData = new HashMap<>();
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

}
