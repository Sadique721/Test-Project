package com.savbill.taskmanagement.rabbitmq.messages;


 import com.savbill.taskmanagement.core.constants.CommonConstants;
 import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
 import lombok.Data;

 import java.util.*;

@Data
public class CafFollowUpMessage {

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
	
	public CafFollowUpMessage(String message, TemplateNotification template, String sourceName, String mobileNo, String emailId, Integer mvnoId, String FollowupDateTime, Integer FollowupTime, String customerName, String staffPersonName, String parentStaffPersonName, String followUpName,Long buId, String username, String altEmail) {

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
        this.customerData.put("username", username);
        this.customerData.put("altEmail",altEmail);

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
