package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.CustMacMappping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustMacMapppingPojo {

	private Integer id;

	private String macAddress;

	private Boolean isDeleted = false;

	private Long leadMasterId;
	
	public CustMacMapppingPojo(CustMacMappping custMacMapping) {
		this.id = custMacMapping.getId();
		this.macAddress = custMacMapping.getMacAddress();
		this.isDeleted = custMacMapping.getIsDeleted();
		if(custMacMapping.getLeadMaster() != null)
			this.leadMasterId = custMacMapping.getLeadMaster().getId();
	}
}
