package com.savbill.radius.dto;

import lombok.Data;

@Data
public class PaginationDTO {
	
	private String fromDate;
	
	private String toDate;

	private int size;
	
	private int page;
}
