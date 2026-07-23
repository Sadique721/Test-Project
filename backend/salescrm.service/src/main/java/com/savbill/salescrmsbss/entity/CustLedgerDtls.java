package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustLedgerDtlsPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "TBLTCUSTLEDGERDTLS")
public class CustLedgerDtls {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTLEDGERDTLSID", nullable = false, length = 40)
    private Integer id;
	
	private String transtype;

    private String transcategory;

    private Double amount;

    @JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

    private Integer creditdocid;

    private Integer debitdocid;

    private String description;
    
    public CustLedgerDtls(CustLedgerDtlsPojo custLedgerDtlsPojo) {
    	this.id = custLedgerDtlsPojo.getId();
    	this.transtype = custLedgerDtlsPojo.getTranstype();
    	this.transcategory = custLedgerDtlsPojo.getTranscategory();
    	this.amount = custLedgerDtlsPojo.getAmount();
    	this.creditdocid = custLedgerDtlsPojo.getCreditdocid();
    	this.debitdocid = custLedgerDtlsPojo.getDebitdocid();
    	this.description = custLedgerDtlsPojo.getDescription();
    	if(custLedgerDtlsPojo.getLeadMasterId() != null)
    		this.leadMaster = new LeadMaster(custLedgerDtlsPojo.getLeadMasterId());
    }
}
