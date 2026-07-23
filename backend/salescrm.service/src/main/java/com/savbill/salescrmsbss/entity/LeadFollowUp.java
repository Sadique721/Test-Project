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

import com.savbill.salescrmsbss.helper.LeadFollowUpDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTLEADFOLLOWUP")
public class LeadFollowUp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_follow_up_id", nullable = false)
	private Long id;

	@Column(name = "follow_up_name")
	private String followUpName;
	
	@Column(name = "follow_up_datetime")
	private LocalDateTime followUpDatetime;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "is_missed ",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isMissed;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "assignee_id")
	private StaffUser staffUser;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "send_reminder_notification")
	private boolean sendReminderNotification;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
	
	@Column(name = "is_send ",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isSend;
	
	@Column(name = "is_no_followup_action",columnDefinition = "Boolean default false", nullable = false)
	private boolean isNoFollowupAction = false;
	
	public LeadFollowUp(LeadFollowUpDto followUpDto,Integer staffId) {
		this.id = followUpDto.getId();
		this.followUpDatetime = followUpDto.getFollowUpDatetime();
		this.followUpName = followUpDto.getFollowUpName();
		this.remarks = followUpDto.getRemarks();
		this.status = followUpDto.getStatus();
		this.isMissed = followUpDto.getIsMissed();
		this.isSend = followUpDto.getIsSend();
		if(staffId != null) {
			this.createdBy = String.valueOf(staffId);
			this.staffUser = new StaffUser(staffId);
		}
		if(followUpDto.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(followUpDto.getLeadMasterId());
	}

	public LeadFollowUp(Long id) {
		this.id = id;
	}
}
