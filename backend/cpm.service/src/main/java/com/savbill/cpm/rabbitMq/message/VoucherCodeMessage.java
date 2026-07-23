package com.savbill.cpm.rabbitMq.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import com.savbill.cpm.modules.Template.domain.TemplateNotification;
import com.savbill.cpm.rabbitMq.RabbitMqConstants;
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
	private boolean isSmsConfigured;
	private boolean isEmailConfigured;

	public VoucherCodeMessage(String countryCode, String mobileNo, String code, Long mvnoId, String sourceName, String message, TemplateNotification template ,Long buId)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("countryCode", countryCode);
		map.put("mobileNumber", mobileNo);
		map.put("code",code);
		map.put("mvnoId", mvnoId);
		if(Objects.nonNull(buId)) {
			map.put(RabbitMqConstants.BU_ID, buId);
		}
		if(Objects.isNull(buId)){
			map.put(RabbitMqConstants.BU_ID, null);
		}
		this.sourceName = sourceName;
		this.setVoucherData(map);
		this.setMessage(message);
		this.setEmailTemplate(template.getEmailTemplateData());
		this.setSmsTemplate(template.getSmsTemplateData());
		this.setAppendUrl(template.getAppendUrl());
		this.isEmailConfigured = template.isEmailEventConfigured();
		this.isSmsConfigured = template.isSmsEventConfigured();
	}

}
