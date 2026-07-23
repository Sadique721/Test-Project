package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRechargeMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String Plan;
    private String purchaseType;
    private String mobileNumber;
    private String emailId;
    private String username;
    private Integer mvnoId;


    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured = true;
    private boolean isEmailConfigured = true;

//    public CustomerRechargeMessage(String username, String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template, String sourceName, String plan, String purchaseType, boolean isEmailConfigured, boolean isSmsConfigured) {
//
//        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.setPlan(Plan);
//        this.sourceName = sourceName;
//        this.purchaseType = purchaseType;
//        this.mvnoId = mvnoId;
//        this.mobileNumber = mobileNumber;
//        this.emailId = emailId;
//        this.username = username;
//        this.isEmailConfigured = isEmailConfigured;
//        this.isSmsConfigured = isSmsConfigured;
//    }
}
