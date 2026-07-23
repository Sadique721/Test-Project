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

import com.savbill.salescrmsbss.helper.LeadSubSourceDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTLEADSUBSOURCE")
public class LeadSubSource{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_sub_source_id", nullable = false)
	private Long id;

	@Column(name = "lead_sub_source_name", nullable = false, length = 250)
	private String leadSubSourceName;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_source_id")
	private LeadSource leadSource;
	
	public LeadSubSource(LeadSubSourceDto leadSubSourceDto)
	{
		this.id = leadSubSourceDto.getId();
		this.leadSubSourceName = leadSubSourceDto.getName();
		LeadSource leadSource2 = new LeadSource();
		leadSource2.setId(leadSubSourceDto.getLeadSourceId());
		this.leadSource = leadSource2;
	}

	public LeadSubSource(Long id) {
		this.id = id;
	}
}
