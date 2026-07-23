package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.savbill.salescrmsbss.helper.LeadNotesDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTLEADNOTES")
public class LeadNotes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_notes_id", nullable = false)
	private Long id;

	@Column(name = "notes", nullable = false)
	private String notes;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_by_name")
	private String createdByName;

	public LeadNotes(LeadNotesDto leadNotesDto,Long staffId) {
		this.id = leadNotesDto.getId();
		this.notes = leadNotesDto.getNotes();
		if(leadNotesDto.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(leadNotesDto.getLeadMasterId());
		this.createdOn = leadNotesDto.getCreatedOn();
		this.createdBy = String.valueOf(staffId);
		this.createdByName = leadNotesDto.getCreatedByName();
	}

}
