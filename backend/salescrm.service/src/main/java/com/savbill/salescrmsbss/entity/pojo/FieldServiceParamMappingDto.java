package com.savbill.salescrmsbss.entity.pojo;

import lombok.Data;

@Data
public class FieldServiceParamMappingDto{

	private Long id;
	private Long fieldid;
	private Long serviceparamid;
	private Boolean is_mandatory;
	private String module;
	private Boolean is_deleted;
	private Integer mvnoId;
	private Integer buId;
	
}
