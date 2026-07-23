package com.savbill.notification.rabbitmq.message;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCodeMessage {
	private String message;
	private String emailTemplate;
	private String smsTemplate;
	private String appendUrl;
	private String sourceName;
	private Map<String,Object> voucherData;
	private String traceId;
	private String spanId;

	private boolean isSmsConfigured;
	private boolean isEmailConfigured;
	
	public VoucherCodeMessage(String countryCode,String mobileNo,String code,Long mvnoId,String sourceName,String message,String emailTemplate,String smsTemplate,String appendUrl, String traceId, String spanId)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("countryCode", countryCode);
		map.put("mobileNumber", mobileNo);
		map.put("code",code);
		map.put("mvnoId", mvnoId);
		this.sourceName = sourceName;
		this.setVoucherData(map);
		this.setMessage(message);
		this.setEmailTemplate(emailTemplate);
		this.setSmsTemplate(smsTemplate);
		this.setAppendUrl(appendUrl);
		this.traceId=traceId;
		this.spanId=spanId;
	}
}
