package com.savbill.notification.helper;

import lombok.Data;

@Data
public class PaginationDTO {
	private String fromDate;

	private String toDate;

	private int size = Integer.MAX_VALUE;	//This is default size if get null

	private int page;
}
