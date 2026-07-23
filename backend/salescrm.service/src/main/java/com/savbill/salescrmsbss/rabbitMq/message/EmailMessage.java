package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.savbill.salescrmsbss.entity.TemplateNotification;

import lombok.Data;

@Data
public class EmailMessage {

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
	private String circuits;

	private String mailIds = "";

	public EmailMessage() {
	}

	public EmailMessage(String message, TemplateNotification template, String sourceName, String mobileNo,
			String customerName, List<String> custMailIds, Long mvnoId, String timestamp, String staffEmail,
			String fileName, String filePath, List<String> circuits, Integer buId) {

		this.setMessage(message);
		this.setSourceName(sourceName);
		this.setEmailTemplate(template.getEmailTemplateData());
		this.setSmsTemplate(template.getSmsTemplateData());
		this.setAppendUrl(template.getAppendUrl());

		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		if (circuits != null && circuits.size() > 0)
			this.circuits = String.join(",", circuits);
		else
			this.circuits = "";

		this.customerData.put("mobileNumber", mobileNo);
		custMailIds.forEach(item -> {
			this.mailIds += item + ",";
		});
		this.customerData.put("custMailIds", this.mailIds);
		this.customerData.put("staffEmail", staffEmail);
		this.customerData.put("mvnoId", mvnoId);
		this.customerData.put("timeStamp", timestamp);
		this.customerData.put("customerName", customerName);
		this.customerData.put("fileName", fileName);
		this.customerData.put("filePath", filePath);
		this.customerData.put("circuits", this.circuits);
		this.customerData.put("buId",buId);

		this.isEmailConfigured = template.isEmailEventConfigured();
		this.isSmsConfigured = template.isSmsEventConfigured();
	}

}
