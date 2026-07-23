package com.savbill.notification.rabbitmq.message;


import lombok.Data;

import java.util.*;

@Data
public class MvnoDocumentDunningMessage {


    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;


    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;


}
