package com.savbill.salescrmsbss.helper;

import com.savbill.salescrmsbss.entity.LeadNotes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadNotesDto {

	private Long id;

	private String notes;
	
	private Long leadMasterId;
	private LocalDateTime createdOn;
	private String createdBy;
	private String createdByName;
	
	public LeadNotesDto(LeadNotes leadNotes) {
		this.id = leadNotes.getId();
		this.notes = leadNotes.getNotes();
		if(leadNotes.getLeadMaster() != null)
			this.leadMasterId = leadNotes.getLeadMaster().getId();
		this.createdOn = leadNotes.getCreatedOn();
		this.createdBy = leadNotes.getCreatedBy();
		this.createdByName = leadNotes.getCreatedByName();
	}

}
