package com.savbill.notification.rabbitmq.message;

import com.savbill.notification.helper.SystemConfigDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigMessage {
	private SystemConfigDTO systemConfig;
	private boolean isUpdate;
	private String traceId;
	private String spanId;
	public ConfigMessage(SystemConfigDTO systemConfig, boolean isUpdate, String traceId, String spanId) {
		this.systemConfig = systemConfig;
		this.isUpdate = isUpdate;
		this.traceId = traceId;
		this.spanId = spanId;
	}
	
}
