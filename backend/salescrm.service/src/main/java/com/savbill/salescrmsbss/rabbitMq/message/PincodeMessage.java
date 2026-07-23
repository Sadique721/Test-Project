package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.Data;

@Data
public class PincodeMessage {

	    private Long id;

	    private String pincode;
	    
	    private String status;
	    
	    private Boolean isDeleted;

	    private Integer countryId;

	    private Integer cityId;

	    private Integer stateId;
	    
	    private Integer mvnoId;
}
