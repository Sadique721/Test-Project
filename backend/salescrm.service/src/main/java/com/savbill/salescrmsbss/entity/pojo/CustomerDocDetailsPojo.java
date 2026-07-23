package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;

import com.savbill.salescrmsbss.entity.CustomerDocDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocDetailsPojo {

    private Long docId;

    private Long leadMasterId;

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
    
    public CustomerDocDetailsPojo(CustomerDocDetails customerDocDetails) {
    	this.docId = customerDocDetails.getDocId();
    	if(customerDocDetails.getLeadMaster() != null) {
    		this.leadMasterId = customerDocDetails.getLeadMaster().getId();
    	}
    	this.docType = customerDocDetails.getDocType();
    	this.docSubType = customerDocDetails.getDocSubType();
    	this.mode = customerDocDetails.getMode();
    	this.remark = customerDocDetails.getRemark();
    	this.docStatus = customerDocDetails.getDocStatus();
    	this.filename = customerDocDetails.getFilename();
    	this.uniquename = customerDocDetails.getUniquename();
    	this.isDelete = customerDocDetails.getIsDelete();
    	this.startDate = customerDocDetails.getStartDate();
    	this.endDate = customerDocDetails.getEndDate();
    	this.documentNumber = customerDocDetails.getDocumentNumber();
    	this.mvnoId = customerDocDetails.getMvnoId();
    }
}
