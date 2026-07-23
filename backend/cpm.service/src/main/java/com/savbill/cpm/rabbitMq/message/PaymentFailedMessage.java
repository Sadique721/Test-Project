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
public class PaymentFailedMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;

    private String customerName ;
    private String currencySymbol;
    private Double paymentAmount;
    private String paymentMode;
    private String mobileNumber;
    private String emailId;

    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String appendUrl;

    private Integer mvnoId;
    private String countryCode;

    private Integer customerId;
    private String reciptNo;
    private String paymentDate;

    private Map<String,Object> customerData = new HashMap<>();


    public PaymentFailedMessage(String message , String customerName, String currencySymbol, Double paymentAmount, String paymentMode, Integer mvnoId, TemplateNotification template, String sourceName, String countryCode, String mobileNumber, String emailId, Integer customerId, String reciptNo, String paymentDate , Long buId, String planName, String password){
        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        customerData.put("userName",customerName);
        customerData.put("currencySymbol",currencySymbol);
        customerData.put("paymentAmount",paymentAmount);
        customerData.put("paymentMode",paymentMode);
        customerData.put("mvnoId",mvnoId);
        customerData.put("emailId",emailId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("countryCode",countryCode);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
        customerData.put("planname", planName);
        customerData.put("password", password);
        customerData.put("userId", customerId);
        customerData.put("reciptNo", reciptNo);
        customerData.put("paymentDate",paymentDate);
        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }

    }

}
