package com.savbill.notification.rabbitmq.message;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpMessage
{
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private String traceId;
	private String spanId;
	private String emailTemplate;
	private String smsTemplate;
	private String appendUrl;
	private String currentUser;
	private Map<String,Object> otpData;
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
	private String datetime;
	private String timeframe;
}
