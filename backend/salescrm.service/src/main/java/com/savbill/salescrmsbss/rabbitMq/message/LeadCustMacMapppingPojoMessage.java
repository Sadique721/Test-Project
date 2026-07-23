package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.CustMacMapppingPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustMacMapppingPojoMessage {

	private Integer id;

	private String macAddress;

	private Boolean isDeleted = false;

	private Long leadMasterId;
	
	public LeadCustMacMapppingPojoMessage(CustMacMapppingPojo custMacMapping) {
		this.id = custMacMapping.getId();
		this.macAddress = custMacMapping.getMacAddress();
		this.isDeleted = custMacMapping.getIsDeleted();
		this.leadMasterId = custMacMapping.getLeadMasterId();
	}
}
