package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDateTime;
import java.util.List;

import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.entity.QuotationPODoc;

import lombok.Data;

@Data
public class QuotationDetailsDTO {

	private Long quotationDetailId;
	private String quotationName;
	private String orgName;
	private List<String> services;
	private LocalDateTime createdOn;
	private Long leadId;
	private Long versionId;
	private Long template_id;
	private String status;
	private QuotationPODoc quotationPODoc;

	private Integer nextApproveStaffId;
	private Integer nextTeamMappingId;
	private Boolean finalApproved;

	public QuotationDetailsDTO() {
	}

	public QuotationDetailsDTO(QuotationDetails instance) {
		if (instance.getId() != null)
			this.quotationDetailId = instance.getId();
		if (instance.getLeadId() != null)
			this.leadId = instance.getLeadId();
		if (instance.getVersionId() != null)
			this.versionId = instance.getVersionId();
		if (instance.getTemplate_id() != null)
			this.template_id = instance.getTemplate_id();
		if (instance.getStatus() != null)
			this.status = instance.getStatus();
		if(instance.getNextApproveStaffId() !=null)
			this.nextApproveStaffId = instance.getNextApproveStaffId();
		this.finalApproved = instance.getFinalApproved();
		this.nextTeamMappingId = instance.getNextTeamMappingId();
	}

}
