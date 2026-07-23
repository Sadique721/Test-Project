package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.CustLedgerDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustLedgerDtlsPojo {

    private Integer id;
	
	private String transtype;

    private String transcategory;

    private Double amount;

	private Long leadMasterId;

    private Integer creditdocid;

    private Integer debitdocid;

    private String description;
    
    public CustLedgerDtlsPojo(CustLedgerDtls custLedgerDtls) {
    	this.id = custLedgerDtls.getId();
    	this.transtype = custLedgerDtls.getTranstype();
    	this.transcategory = custLedgerDtls.getTranscategory();
    	this.amount = custLedgerDtls.getAmount();
    	this.creditdocid = custLedgerDtls.getCreditdocid();
    	this.debitdocid = custLedgerDtls.getDebitdocid();
    	this.description = custLedgerDtls.getDescription();
    	if(custLedgerDtls.getLeadMaster() != null)
    		this.leadMasterId = custLedgerDtls.getLeadMaster().getId();
    }
}
