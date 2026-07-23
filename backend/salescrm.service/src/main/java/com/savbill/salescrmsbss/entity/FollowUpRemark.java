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

import com.savbill.salescrmsbss.helper.FollowUpRemarkDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTFOLLOWUPREMARK")
public class FollowUpRemark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_up_remark_id", nullable = false)
	private Long id;

	@Column(name = "remark")
	private String remark;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_follow_up_id")
	private LeadFollowUp leadFollowUp;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
	
	public FollowUpRemark(FollowUpRemarkDto followUpRemarkDto) {
		this.id = followUpRemarkDto.getId();
		this.remark = followUpRemarkDto.getRemark();
		this.leadFollowUp = new LeadFollowUp(followUpRemarkDto.getLeadFollowUpId());
	}
}
