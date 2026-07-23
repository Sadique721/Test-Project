package com.savbill.salescrmsbss.helper;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import com.savbill.salescrmsbss.entity.LeadFollowUp;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadFollowUpDto {

	private Long id;

	private String followUpName;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime followUpDatetime;
	
	private String remarks;
	
	private String status;
	
    private Boolean isMissed = false;
    
    private Boolean isSend = false;
	
	private Long leadMasterId;
	
	private String leadMasterName;
	
	private Integer createdBy;
	
	private String staffName;
	
	public LeadFollowUpDto(LeadFollowUp leadFollowUp) {
		this.id = leadFollowUp.getId();
		this.followUpName = leadFollowUp.getFollowUpName();
		this.followUpDatetime = leadFollowUp.getFollowUpDatetime();
		this.remarks = leadFollowUp.getRemarks();
		this.status = leadFollowUp.getStatus();
		this.isMissed = leadFollowUp.getIsMissed();
		this.isSend = leadFollowUp.getIsSend();
		if(leadFollowUp.getLeadMaster() != null) {			
			String customerName = "";
			this.leadMasterId = leadFollowUp.getLeadMaster().getId();
			if (!StringUtils.isEmpty(leadFollowUp.getLeadMaster().getLastname()))
				customerName += leadFollowUp.getLeadMaster().getFirstname() + " " + leadFollowUp.getLeadMaster().getLastname();
			else
				customerName += leadFollowUp.getLeadMaster().getFirstname();
			this.leadMasterName = customerName;
		}
		if(leadFollowUp.getStaffUser() != null) {			
			this.createdBy = leadFollowUp.getStaffUser().getId();
			this.staffName = leadFollowUp.getStaffUser().getFirstname()+" "+leadFollowUp.getStaffUser().getFirstname();
		}
	}
}
