package com.savbill.ticketmanagement.rabbitmq.messages;


import com.savbill.ticketmanagement.core.constants.CommonConstants;
import com.savbill.ticketmanagement.core.modules.Template.domain.TemplateNotification;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Data
@Getter
@Setter
public class CustTicketStatusMessage {

    private String messageId;
    private Date messageDate;
    private String username;

   private Integer ticketnumber;
    private String message;

    private String emailTemplate;

    private String smsTemplate;

    private String emailId;

    private Integer mvnoId;

    private String mobileNumber;

    private String sourceName;

    private String countryCode;
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private Map<String, Object> customerData = new HashMap<>();
    public Map<String, Object> getCustomerData() {
        return customerData;
    }
    public CustTicketStatusMessage(String username, String status, Map<String, Object>customerData , String countryCode,
                                   String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template,
                                   String sourceName, Integer ticketnumber,Long buId, String caseNumber, String altEmail){

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message=message;
        this.message = "customer ticket status change for "+caseNumber;
        this.ticketnumber=ticketnumber;
        this.username=username;
        this.mobileNumber=mobileNumber;
        this.emailId=emailId;
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
        this.mvnoId= mvnoId;
        this.countryCode=countryCode;
        this.emailTemplate=template.getEmailTemplateData();
        this.smsTemplate=template.getSmsTemplateData();

        this.customerData.put("smstemplate",template.getSmsTemplateData());
        this.customerData.put("emailTemplate",template.getEmailTemplateData());
        this.customerData.put("mvnoId", mvnoId);
        this.customerData.put("emailId",emailId);
        this.customerData.put("username",username);
        this.customerData.put("mobileNumber",mobileNumber);
        this.customerData.put("ticketnumber",caseNumber);
        this.customerData.put("status", status);
        this.customerData.put("altEmail",altEmail);
        if(Objects.nonNull(buId)){
            this.customerData.put(CommonConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(CommonConstants.BU_ID,null);
        }
        this.sourceName=sourceName;

    }
}
