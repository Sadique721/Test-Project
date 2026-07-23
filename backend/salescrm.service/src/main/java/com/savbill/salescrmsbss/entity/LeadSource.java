package com.savbill.salescrmsbss.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.savbill.salescrmsbss.helper.LeadSourceDto;
import com.savbill.salescrmsbss.helper.LeadSubSourceDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMLEADSOURCE")
public class LeadSource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_source_id", nullable = false)
	private Long id;

	@Column(name = "lead_source_name", nullable = false, length = 250)
	private String leadSourceName;

	@Column(name = "status", nullable = false, length = 250)
	private String status;

	@JsonManagedReference
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.FALSE)
	@EqualsAndHashCode.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadSource")
	private List<LeadSubSource> leadSubSourceList = new ArrayList<>();

	@Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
	
	@Column(name = "mvno_id")
	private Long mvnoId;

	@Column(name = "bu_id")
	private Long buId;
	
	@Column(name = "view")
	private Boolean view = false;
	
	public LeadSource(LeadSourceDto leadSourceDto,Long mvnoId,Long buId)
	{
		List<LeadSubSource> leadSubSourceList = new ArrayList<LeadSubSource>();
		this.id = leadSourceDto.getId();
		this.leadSourceName = leadSourceDto.getLeadSourceName();
		this.status = leadSourceDto.getStatus();
		this.mvnoId = mvnoId;
		this.buId = buId;
		this.view = leadSourceDto.getView();
		for(LeadSubSourceDto leadSubSourceDto : leadSourceDto.getLeadSubSourceDtoList()) {
			leadSubSourceList.add(new LeadSubSource(leadSubSourceDto));
		}
		this.leadSubSourceList = leadSubSourceList;
	}

	public LeadSource(Long id) {
		this.id = id;
	}


	public LeadSource(LeadSource leadSource) {

		this.id = leadSource.id;
		this.leadSourceName = leadSource.leadSourceName;
		this.status = leadSource.status;
		this.leadSubSourceList = leadSource.leadSubSourceList;
		this.isDelete = leadSource.isDelete;
		this.mvnoId = leadSource.mvnoId;
		this.buId = leadSource.buId;
		this.view = leadSource.view;
	}
}
