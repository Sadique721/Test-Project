package com.savbill.salescrmsbss.rabbitMq;

import lombok.Data;

@Data
public class ClientServiceMessage {
	
	private Integer id;

	private String name;

	private String value;
	
	private Long mvnoId;

}
