package com.savbill.taskmanagement.rabbitmq.messages;


import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.constants.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTatReminderNotification {

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

    public TicketTatReminderNotification(String message, TemplateNotification template, String sourceName, String mobileNo, String emailId, Integer mvnoId, String staffPersonName, String parentStaffName, String caseNumber, Long buId, LocalDateTime localDateTime) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", mobileNo);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoId);
        this.customerData.put("caseNumber", caseNumber);
        this.customerData.put("staffPersonName", staffPersonName);
        this.customerData.put("parentStaffPersonName", parentStaffName);
        if(Objects.nonNull(buId)){
            this.customerData.put(CommonConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(CommonConstants.BU_ID,null);
        }
        this.customerData.put("eventName", "Task");
        DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            String formattedDateTime = localDateTime.format(formatterWithSeconds);
            this.customerData.put("Assigndatetime", formattedDateTime);
        } catch (Exception e) {
            String formattedDateTime = localDateTime.format(formatterWithoutSeconds);
            this.customerData.put("Assigndatetime", formattedDateTime);
        }

        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
