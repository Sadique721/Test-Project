package com.savbill.notification.rabbitmq.message;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDunningMessage {


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

//    public CustomerDunningMessage(String message,TemplateNotification template,String sourceName,Customers customer,String amount,String currency){
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
//        this.customerData.put("mobileNumber",customer.getMobile());
//        this. customerData.put("emailId",customer.getEmail());
//        this.customerData.put("mvnoId",customer.getMvnoId());
//        this.customerData.put("username",customer.getUsername());
//        this.customerData.put("amount",amount);
//        this.customerData.put("countryCode",customer.getCountryCode());
//        this.customerData.put("currency",currency);
//
//
//        this.isEmailConfigured = template.isEmailEventConfigured();
//        this.isSmsConfigured = template.isSmsEventConfigured();
//
//    }

}
