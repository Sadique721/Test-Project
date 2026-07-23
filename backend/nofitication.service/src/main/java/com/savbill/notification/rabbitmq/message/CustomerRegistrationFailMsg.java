package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationFailMsg {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String registrationStatus;
    private String password;

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured = true;
    private boolean isEmailConfigured = true;

    private String mobileNumber;
    private String emailId;
    private String username;
    private Integer mvnoId;

//    public CustomerRegistrationFailMsg(String username, String password, String  mobileNumber, String emailId, Integer mvnoId,  String message, TemplateNotification template, String sourceName, String status, boolean isEmailConfigured, boolean isSmsConfigured) {
//
//        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.setRegistrationStatus(status);
//        this.sourceName = sourceName;
//        customerData.put("username",username);
//        customerData.put("mvnoId",mvnoId);
//        customerData.put("mobileNumber",mobileNumber);
//        customerData.put("emailId",emailId);
//        customerData.put("password",password);
//        this.isEmailConfigured = isEmailConfigured;
//        this.isSmsConfigured = isSmsConfigured;
//    }


}
