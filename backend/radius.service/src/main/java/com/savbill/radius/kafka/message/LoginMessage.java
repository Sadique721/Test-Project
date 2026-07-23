package com.savbill.radius.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginMessage {
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private Map<String,Object> loginData;
}
