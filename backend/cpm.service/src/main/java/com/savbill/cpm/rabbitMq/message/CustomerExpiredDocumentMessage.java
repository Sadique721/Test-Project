package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.Template.domain.TemplateNotification;
import com.savbill.cpm.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class CustomerExpiredDocumentMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String staffName;


    private Map<String, Object> partnerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public CustomerExpiredDocumentMessage(String message, TemplateNotification template, String sourceName, Customers customers, String staffName) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setStaffName(staffName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.partnerData.put("mobileNumber", customers.getMobile());
        this.partnerData.put("emailId", customers.getEmail());
        this.partnerData.put("mvnoId", customers.getMvnoId());
        this.partnerData.put("partnerName", customers.getFirstname());
        this.partnerData.put("countryCode", customers.getCountryCode());
        this.partnerData.put("staffName" , staffName);
        if(Objects.nonNull(customers.getBuId())){
            this.partnerData.put(RabbitMqConstants.BU_ID,customers.getBuId());
        }
        else{
            this.partnerData.put(RabbitMqConstants.BU_ID,null);
        }


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
