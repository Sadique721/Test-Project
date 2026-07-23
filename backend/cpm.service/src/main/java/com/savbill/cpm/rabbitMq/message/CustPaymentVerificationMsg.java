package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.modules.Template.domain.TemplateNotification;
import com.savbill.cpm.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustPaymentVerificationMsg {
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
    private String reciptNo;
    private Double paymentAmount;
    private String paymentDate;

    public CustPaymentVerificationMsg(String username, String  mobileNumber, String emailId, String status, Integer mvnoId, String reciptNo, Double paymentAmount, String paymentDate, String message, TemplateNotification template, String sourceName,Long buId){
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
        customerData.put("reciptNo", reciptNo);
        customerData.put("paymentAmount", paymentAmount);
        customerData.put("paymentDate", paymentDate);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }

        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }


}
