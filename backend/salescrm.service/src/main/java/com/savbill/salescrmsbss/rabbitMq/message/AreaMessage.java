package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.Data;

@Data
public class AreaMessage {

	private Long id;

	private String name;
	
	private String status;
	
	private Boolean isDeleted;

	private Integer countryId;

	private Integer cityId;

	private Integer stateId;
	
	private Integer pincodeId;

	private Integer mvnoId;
}
