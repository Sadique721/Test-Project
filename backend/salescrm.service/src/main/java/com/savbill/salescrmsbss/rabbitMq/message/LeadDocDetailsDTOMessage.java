package com.savbill.salescrmsbss.rabbitMq.message;

import java.time.format.DateTimeFormatter;

import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadDocDetailsDTOMessage {

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
        
    private String startDate;

    private String endDate;
    
    public LeadDocDetailsDTOMessage(LeadDocDetailsDTO leadDocDetails) {
    	this.docId = leadDocDetails.getDocId();
		this.docType = leadDocDetails.getDocType();
		this.docSubType = leadDocDetails.getDocSubType();
		this.mode = leadDocDetails.getMode();
		this.remark = leadDocDetails.getRemark();
		this.docStatus = leadDocDetails.getDocStatus();
		this.filename = leadDocDetails.getFilename();
		this.uniquename = leadDocDetails.getUniquename();
		this.isDelete = leadDocDetails.getIsDelete();
		if (leadDocDetails.getStartDate() != null) {
			this.startDate = leadDocDetails.getStartDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		if (leadDocDetails.getEndDate() != null) {
			this.endDate = leadDocDetails.getEndDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		this.leadMasterId = leadDocDetails.getLeadMasterId();
    }
}
