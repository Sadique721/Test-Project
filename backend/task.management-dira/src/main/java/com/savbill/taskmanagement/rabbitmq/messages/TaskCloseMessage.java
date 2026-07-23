package com.savbill.taskmanagement.rabbitmq.messages;


import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
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



    public TaskCloseMessage(String staffName, String mobileNumber, String emailId, String message, TemplateNotification template, String caseNumber, Integer mvnoId, String caseStatus, String casePriority, String caseRemark, String startDate, String endDate, String taskFor) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        if(Objects.nonNull(template)) {
            this.setEmailTemplate(template.getEmailTemplateData() != null ? template.getEmailTemplateData() : null);
            this.setSmsTemplate(template.getSmsTemplateData() != null ? template.getSmsTemplateData() : null);
            this.setAppendUrl(template.getAppendUrl() != null ? template.getAppendUrl() : null);
            this.isEmailConfigured = template.isEmailEventConfigured();
            this.isSmsConfigured = template.isSmsEventConfigured();
        }
        this.messageDate = new Date();
        this.message="Task Close Email";
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("customerName",staffName);
        this.customerData.put("username",staffName);
        this.customerData.put("mobileNumber",mobileNumber);
        this.customerData.put("emailId",emailId);
        //this.customerData.put("name",name);
        this.customerData.put("caseNumber",caseNumber);
//      this.customerData.put("Assigndatetime",assigndatetime);
//      this.customerData.put("parentStaffPersonName",parentStaffPersonName);
        //this.customerData.put("eventName",eventName);
        this.customerData.put("caseStatus",caseStatus);
        this.customerData.put("casePriority", casePriority);
        this.customerData.put("caseRemark",caseRemark);
        this.customerData.put("startDate",startDate);
        this.customerData.put("endDate",endDate);
        this.customerData.put("taskFor",taskFor);
        this.customerData.put("mvnoId",mvnoId);

    }
}
