package com.savbill.radius.kafka;

import com.savbill.radius.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
//@NoArgsConstructor
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

	public CustomerMessage() {
		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		this.message = message;
	}

	public CustomerMessage(Customer customerDto, String password, String message, String emailTemplate, String smsTemplate, String appendUrl) {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageConstants.USER_NAME, customerDto.getUserName());
		map.put(MessageConstants.PASSWORD, password);
		map.put(MessageConstants.EMAIL_ID, customerDto.getEmailAddress());
		map.put(MessageConstants.MOBILE_NUMBER, customerDto.getMobileNo());
		map.put(MessageConstants.COUNTRY_CODE, customerDto.getCountryCode());
		map.put("mvnoId", customerDto.getMvnoId());
		map.put(MessageConstants.SLICE_CHUNK, customerDto.getSliceChunk());
		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		this.message = message;
		this.setEmailTemplate(emailTemplate);
		this.setSmsTemplate(smsTemplate);
		this.setAppendUrl(appendUrl);
		this.customerData = map;
		this.sourceName = MessageConstants.SOURCE_NAME_SAVBILL_RADIUS;
//		if(customerDto.getSourceName() != null)
//	   	{
//	   		this.sourceName = customerDto.getSourceName(); 
//	   	}
//	   	else
//	   	{
//	   		this.sourceName = RabbitMqConstants.SOURCE_NAME_SAVBILL_RADIUS;
//	   	}
	}
}
