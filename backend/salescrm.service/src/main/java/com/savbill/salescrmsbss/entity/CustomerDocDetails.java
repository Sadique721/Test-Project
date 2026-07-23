package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustomerDocDetailsPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblcustdocdetails")
public class CustomerDocDetails {
   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_master_id")
    private LeadMaster leadMaster;

    private String docType;
    
    private String docSubType;
    
    private String mode;
    
    private String remark;
    
    private String docStatus;
    
    private String filename;
    
    private String uniquename;
    
    private Boolean isDelete;
    
    private LocalDate startDate;

    private LocalDate endDate;
    
    private String documentNumber;
    
    private Integer mvnoId;
    
    public CustomerDocDetails(CustomerDocDetailsPojo customerDocDetailsPojo) {
    	this.docId = customerDocDetailsPojo.getDocId();
    	if(customerDocDetailsPojo.getLeadMasterId() != null) {
    		this.leadMaster = new LeadMaster(customerDocDetailsPojo.getLeadMasterId());
    	}
    	this.docType = customerDocDetailsPojo.getDocType();
    	this.docSubType = customerDocDetailsPojo.getDocSubType();
    	this.mode = customerDocDetailsPojo.getMode();
    	this.remark = customerDocDetailsPojo.getRemark();
    	this.docStatus = customerDocDetailsPojo.getDocStatus();
    	this.filename = customerDocDetailsPojo.getFilename();
    	this.uniquename = customerDocDetailsPojo.getUniquename();
    	this.isDelete = customerDocDetailsPojo.getIsDelete();
    	this.startDate = customerDocDetailsPojo.getStartDate();
    	this.endDate = customerDocDetailsPojo.getEndDate();
    	this.documentNumber = customerDocDetailsPojo.getDocumentNumber();
    	this.mvnoId = customerDocDetailsPojo.getMvnoId();
    }

}

