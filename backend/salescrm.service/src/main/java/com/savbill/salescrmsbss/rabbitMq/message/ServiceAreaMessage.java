package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.Data;

@Data
public class ServiceAreaMessage {

	private Long id;

	private String name;

	private String status;

	private Boolean isDeleted;

	private Integer mvnoId;
	private Integer buId;

	private String latitude;

	private String longitude;

	private Long areaId;
}
