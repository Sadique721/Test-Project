package com.savbill.ticketmanagement.rabbitmq.messages;


import com.savbill.ticketmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.ticketmanagement.core.constants.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustTicketCloseMsg {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    private String username;
    private String mobileNumber;
    private String emailId;
    private String status;
    private Integer mvnoId;
    private String caseNumber;

    public CustTicketCloseMsg(String username, String  mobileNumber, String emailId, String status, Integer mvnoId, String caseNumber, String message, TemplateNotification template, String sourceName,Long buId, String caseTitle, String altEmail){
        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        customerData.put("username",username);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("status", status);
        customerData.put("mvnoId", mvnoId);
        customerData.put("caseNumber", caseNumber);
        customerData.put("caseTitle" , caseTitle);
        customerData.put("altEmail",altEmail);
        if(Objects.nonNull(buId)){
            this.customerData.put(CommonConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(CommonConstants.BU_ID,null);
        }


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }


}
