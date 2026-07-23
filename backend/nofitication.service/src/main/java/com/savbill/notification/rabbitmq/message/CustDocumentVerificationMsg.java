package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustDocumentVerificationMsg {
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
    private String username;
    private String mobileNumber;
    private String emailId;
    private String status;
    private Integer mvnoId;
}