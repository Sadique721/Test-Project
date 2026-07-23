package com.savbill.salescrmsbss.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.Email;

import lombok.Data;

@Data
public class EmailAuditingDTO {

	private Long emailDTOId;

	private List<String> custMailAddresses;

	private Long staffId;

	private String subject;

	private Long mvnoId;
	private Long quotationId;
	
	private Long leadId;
	private String body;
	private String file;

	public EmailAuditingDTO() {
	}

	public EmailAuditingDTO(Email email) {

		if (email.getEmailId() != null)
			this.emailDTOId = email.getEmailId();
		if (email.getQuotationDetails() != null && email.getQuotationDetails().getId() != null)
			this.quotationId = email.getQuotationDetails().getId();
		if (email.getMessage() != null)
			this.subject = email.getMessage();
		if (email.getMvnoId() != null)
			this.mvnoId = email.getMvnoId();
		if (email.getEmailAddress() != null && !email.getEmailAddress().equalsIgnoreCase("")) {
			List<String> emailAddresses = new ArrayList<>();
			emailAddresses.add(email.getEmailAddress());
		}
		if (email.getStaffId() != null)
			this.staffId = email.getStaffId();
		if(email.getLeadId()!= null)
			this.leadId = email.getLeadId();
		if(email.getEmailContent() != null)
			this.body = email.getEmailContent();
	}

}
