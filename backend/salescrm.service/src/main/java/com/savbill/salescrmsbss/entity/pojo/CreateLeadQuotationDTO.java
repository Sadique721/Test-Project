package com.savbill.salescrmsbss.entity.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateLeadQuotationDTO {

	List<Long> leadServiceMappingIdList;
	private String validityUnit;
	private Integer validity;
	private String installationUnit;
	private Integer installationValidity;
	
}
