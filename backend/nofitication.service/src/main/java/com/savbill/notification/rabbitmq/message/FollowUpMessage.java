package com.savbill.notification.rabbitmq.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

//@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	public String getSmsTemplate() {
		return smsTemplate;
	}
	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}
	public String getAppendUrl() {
		return appendUrl;
	}
	public void setAppendUrl(String appendUrl) {
		this.appendUrl = appendUrl;
	}
	public Map<String, Object> getCustomerData() {
		return customerData;
	}
	public void setCustomerData(Map<String, Object> customerData) {
		this.customerData = customerData;
	}
	public boolean isSmsConfigured() {
		return isSmsConfigured;
	}
	public void setSmsConfigured(boolean isSmsConfigured) {
		this.isSmsConfigured = isSmsConfigured;
	}
	public boolean isEmailConfigured() {
		return isEmailConfigured;
	}
	public void setEmailConfigured(boolean isEmailConfigured) {
		this.isEmailConfigured = isEmailConfigured;
	}
    
    

}
