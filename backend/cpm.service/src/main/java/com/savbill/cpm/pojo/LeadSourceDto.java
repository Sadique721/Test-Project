package com.savbill.cpm.pojo;

import com.savbill.cpm.model.lead.LeadSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadSourceDto {

	private Long id;

	private String leadSourceName;

	private String status;
		
	private Long mvnoId;

	private Long buId;
	
	public LeadSourceDto(LeadSource leadSource) {
		this.id = leadSource.getId();
		this.leadSourceName = leadSource.getLeadSourceName();
		this.status = leadSource.getStatus();
		this.mvnoId = leadSource.getMvnoId();
		this.buId = leadSource.getBuId();
	}
}
