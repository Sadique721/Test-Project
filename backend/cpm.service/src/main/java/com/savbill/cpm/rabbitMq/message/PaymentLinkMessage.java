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
public class PaymentLinkMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;

    private String mobileNumber;
    private String emailId;

    private String customerName ;
    private String currencySymbol;
    private Double paymentAmount;
    private String url1;
    private String url2;
    private Integer mvnoId;
    private String appendUrl;


    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private Map<String,Object> customerData = new HashMap<>();

    public PaymentLinkMessage(String message, String customerName, String currencySymbol, Double paymentAmount, String url, Integer mvnoId, TemplateNotification template, String sourceName, String countryCode, String mobileNumber, String emailId,Long buId){

        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;

        customerData.put("emailId",emailId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("customerName",customerName);
        customerData.put("currencySymbol",currencySymbol);
        customerData.put("paymentAmount",paymentAmount);
        customerData.put("url",url);
        customerData.put("countryCode",countryCode);
        customerData.put("mvnoId",mvnoId);
        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();


    }


}
