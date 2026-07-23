package com.savbill.radius.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerMessage {
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private Map<String,Object> customerData;

	public CustomerMessage() {
		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		this.message = "Customer's used data updates";
	}
}
