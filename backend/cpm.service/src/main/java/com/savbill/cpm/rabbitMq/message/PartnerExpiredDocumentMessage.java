package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.Template.domain.TemplateNotification;
import com.savbill.cpm.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class PartnerExpiredDocumentMessage {
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

    public PartnerExpiredDocumentMessage(String message, TemplateNotification template, String sourceName, Partner partner, String staffName) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setStaffName(staffName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.partnerData.put("mobileNumber", partner.getMobile());
        this.partnerData.put("emailId", partner.getEmail());
        this.partnerData.put("mvnoId", partner.getMvnoId());
        this.partnerData.put("buId", partner.getBuId());
        this.partnerData.put("partnerName", partner.getName());
        this.partnerData.put("countryCode", partner.getCountryCode());
        this.partnerData.put("staffName" , staffName);
        if(Objects.nonNull(partner.getBuId())){
            this.partnerData.put(RabbitMqConstants.BU_ID,partner.getBuId());
        }
        else{
            this.partnerData.put(RabbitMqConstants.BU_ID,null);
        }


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
