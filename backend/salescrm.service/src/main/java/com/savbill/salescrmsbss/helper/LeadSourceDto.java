package com.savbill.salescrmsbss.helper;

import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.LeadSource;
import com.savbill.salescrmsbss.entity.LeadSubSource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadSourceDto {

	private Long id;

	private String leadSourceName;

	private String status;
	
	private Boolean view = false;

	private List<LeadSubSourceDto> leadSubSourceDtoList;
	
	private List<Long> leadSubSourceDeletedIds;

	private Long mvnoId;

	private Long buId;

	public LeadSourceDto(LeadSource leadSource) {
		List<LeadSubSourceDto> leadSubSourceDtoList = new ArrayList<LeadSubSourceDto>();
		this.id = leadSource.getId();
		this.leadSourceName = leadSource.getLeadSourceName();
		this.status = leadSource.getStatus();
		this.view = leadSource.getView();
		this.mvnoId = leadSource.getMvnoId();
		this.buId = leadSource.getBuId();
		if (leadSource.getLeadSubSourceList() != null) {
			for (LeadSubSource leadSubSource : leadSource.getLeadSubSourceList()) {
				leadSubSourceDtoList.add(new LeadSubSourceDto(leadSubSource));
			}
		}
		this.leadSubSourceDtoList = leadSubSourceDtoList;
	}

}
