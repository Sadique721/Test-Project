package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.CustomerLedger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLedgerPojo {

	private Integer id;

	private Double totaldue;

	private Double totalpaid;
	
	public CustomerLedgerPojo(CustomerLedger customerLedger) {
		this.id = customerLedger.getId();
		this.totaldue = customerLedger.getTotaldue();
		this.totalpaid = customerLedger.getTotalpaid();
	}
}
