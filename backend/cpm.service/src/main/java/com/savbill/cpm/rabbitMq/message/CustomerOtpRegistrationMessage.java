package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.Template.domain.TemplateNotification;
import com.savbill.cpm.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOtpRegistrationMessage {

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



    public CustomerOtpRegistrationMessage(String message ,TemplateNotification template, String sourceName,Customers customers)
    {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", customers.getMobile());
        this.customerData.put("emailId", customers.getEmail());
        this.customerData.put("mvnoId", customers.getMvnoId());
        this.customerData.put("username", customers.getUsername());
        this.customerData.put("password" , customers.getPassword());
        this.customerData.put("countryCode", customers.getCountryCode());
        if(Objects.nonNull(customers.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,customers.getBuId());
        }
        if(Objects.isNull(customers.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }

        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }

}
