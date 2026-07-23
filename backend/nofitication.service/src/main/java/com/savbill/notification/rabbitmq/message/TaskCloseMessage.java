package com.savbill.notification.rabbitmq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCloseMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private String username;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;
    private String countryCode = "+91";
    private String caseNumber;  // Id
    private String caseStatus; // status
    private String priority; // priority
    private String remark ; //remark
    private String name;
    private LocalDate nextFollowupDate;


    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String parentStaffPersonName;

    private String staffPersonName;

    private String eventName;

    private String assigndatetime;

    private String type;

    public TaskCloseMessage(String username, String mobileNumber, String emailId, String emailTemplate, String smsTemplate, String caseNumber, Integer mvnoId, String caseStatus, String priority, String remark, String appendUrl, String message, String type) {
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.emailTemplate = emailTemplate;
        this.smsTemplate = smsTemplate;
        this.caseNumber = caseNumber;
        this.mvnoId = mvnoId;
        this.caseStatus = caseStatus;
        this.priority = priority;
        this.remark = remark;
        this.appendUrl = appendUrl;
        this.message = message;
        this.type = type;

        // default values
        this.countryCode = "+91";
        this.messageDate = new Date();
        this.isEmailConfigured = true;
        this.isSmsConfigured = true;
    }




}
