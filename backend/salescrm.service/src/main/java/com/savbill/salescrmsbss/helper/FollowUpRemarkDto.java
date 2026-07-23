package com.savbill.salescrmsbss.helper;

import com.savbill.salescrmsbss.entity.FollowUpRemark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpRemarkDto {

	private Long id;

	private String remark;
	
	private Long leadFollowUpId;
	
	public FollowUpRemarkDto(FollowUpRemark followUpRemark) {
		this.id = followUpRemark.getId();
		this.remark = followUpRemark.getRemark();
		this.leadFollowUpId = followUpRemark.getLeadFollowUp().getId();
	}
	
}
