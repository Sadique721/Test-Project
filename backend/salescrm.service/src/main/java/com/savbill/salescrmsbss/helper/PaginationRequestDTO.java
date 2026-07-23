package com.savbill.salescrmsbss.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PaginationRequestDTO {

	private Integer page;
	private Integer pageSize;
	private Integer sortOrder;
	private String sortBy;
	private List<GenericSearchModel> filters = new ArrayList<>();
	private String status;
	private String filterBy;
	private List<Long> buids;
}
