package com.savbill.notification.rabbitmq.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
