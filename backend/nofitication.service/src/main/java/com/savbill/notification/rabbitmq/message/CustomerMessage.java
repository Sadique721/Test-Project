package com.savbill.notification.rabbitmq.message;

import java.util.Date;
import java.util.Map;

import com.google.inject.spi.StaticInjectionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMessage 
{
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private String emailTemplate;
	private String smsTemplate;
	private String appendUrl;
	private Map<String,Object> customerData;
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
	private String traceId;
	private String spanId;

}
