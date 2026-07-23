package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketFollowUpMessage {
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

//    public TicketFollowUpMessage(String message, TemplateNotification template, String sourceName, String mobileNo, String emailId, Integer mvnoId, String FollowupDateTime, Integer FollowupTime, String caseNumber, String staffPersonName, String parentStaffPersonName, String followUpName) {
//
//        this.setMessage(message);
//        this.setSourceName(sourceName);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//
//        this.customerData.put("mobileNumber", mobileNo);
//        this.customerData.put("emailId", emailId);
//        this.customerData.put("mvnoId", mvnoId);
//        this.customerData.put("followupDateTime", FollowupDateTime);
//        this.customerData.put("followupTime", FollowupTime);
//        this.customerData.put("caseNumber", caseNumber);
//        this.customerData.put("staffPersonName", staffPersonName);
//        this.customerData.put("parentStaffPersonName", parentStaffPersonName);
//        this.customerData.put("followUpName", followUpName);
//
//
//        this.isEmailConfigured = template.isEmailEventConfigured();
//        this.isSmsConfigured = template.isSmsEventConfigured();
//
//    }
}
