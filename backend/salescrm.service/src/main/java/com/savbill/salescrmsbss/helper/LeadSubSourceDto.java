package com.savbill.salescrmsbss.helper;

import com.savbill.salescrmsbss.entity.LeadSubSource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadSubSourceDto {

	private Long id;
	
	private String name;
	
	private Long leadSourceId;
	
   public LeadSubSourceDto(LeadSubSource leadSubSource) {
	   this.id = leadSubSource.getId();
	   this.name = leadSubSource.getLeadSubSourceName();
	   this.leadSourceId = leadSubSource.getLeadSource().getId();
	}
}
