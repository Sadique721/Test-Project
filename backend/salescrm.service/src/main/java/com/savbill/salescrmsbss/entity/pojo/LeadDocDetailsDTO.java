package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;

import com.savbill.salescrmsbss.entity.LeadDocDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadDocDetailsDTO {
	
	private Long docId;
	    
    private String docType;
    
    private String docSubType;
    
    private String remark;
    
    private String mode;
    
    private String docStatus;
    
    private String filename;
    
    private String uniquename;
    
    private Boolean isDelete = false;
    
    private String documentNumber;

	private Long leadMasterId;
        
    private LocalDate startDate;

    private LocalDate endDate;
	private Integer staffId;

    public LeadDocDetailsDTO(LeadDocDetails leadDocDetails) {
    	this.docId = leadDocDetails.getDocId();
		this.docType = leadDocDetails.getDocType();
		this.docSubType = leadDocDetails.getDocSubType();
		this.mode = leadDocDetails.getMode();
		this.remark = leadDocDetails.getRemark();
		this.docStatus = leadDocDetails.getDocStatus();
		this.filename = leadDocDetails.getFilename();
		this.uniquename = leadDocDetails.getUniquename();
		this.isDelete = leadDocDetails.getIsDelete();
		this.startDate = leadDocDetails.getStartDate();
		this.endDate = leadDocDetails.getEndDate();
		if(leadDocDetails.getLeadMaster() != null)
			this.leadMasterId = leadDocDetails.getLeadMaster().getId();
		this.staffId=leadDocDetails.getLeadMaster().getNextApproveStaffId();
    }

}
