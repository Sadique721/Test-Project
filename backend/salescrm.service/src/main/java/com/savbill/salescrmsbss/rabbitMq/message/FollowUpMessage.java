package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.savbill.salescrmsbss.entity.TemplateNotification;

import lombok.Data;

@Data
public class FollowUpMessage {

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
	
	public FollowUpMessage(String message, TemplateNotification template, String sourceName, String mobileNo,String emailId,Integer mvnoId,String FollowupDateTime,Integer FollowupTime,String customerName,String staffPersonName,String parentStaffPersonName,String followUpName,Integer buId) {

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
        this.customerData.put("followupDateTime", FollowupDateTime);
        this.customerData.put("followupTime", FollowupTime);
        this.customerData.put("customerName", customerName);
        this.customerData.put("staffPersonName", staffPersonName);
        this.customerData.put("parentStaffPersonName", parentStaffPersonName);
        this.customerData.put("followUpName", followUpName);
        this.customerData.put("buId",buId);


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }

}
