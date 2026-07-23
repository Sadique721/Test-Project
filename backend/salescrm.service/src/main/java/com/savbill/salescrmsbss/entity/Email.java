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

import com.savbill.salescrmsbss.entity.pojo.EmailAuditingDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "TBLTEMAIL")
@ApiModel(value = "Email Entity", description = "This is Email entity which is used to update email data")
@Data
public class Email {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated email Id")
	@Column(name = "email_id", nullable = false)
	private Long emailId;

	@ApiModelProperty(notes = "This is Source name of service", required = true)
	@Column(name = "sourcename", length = 100)
	private String sourceName;

	@ApiModelProperty(notes = "This is Email Address", required = true)
	@Column(name = "emailaddress", length = 100)
	private String emailAddress;

	@ApiModelProperty(notes = "This is message for email", required = true)
	@Column(name = "message", length = 500)
	private String message;

	@ApiModelProperty(notes = "This is date for email", required = false)
	@Column(name = "date")
	@JsonFormat(pattern = "yyyy-MM-dd, HH:mm:ss")
	private LocalDateTime date = LocalDateTime.now();

	@ApiModelProperty(notes = "This is status of email", required = false)
	@Column(name = "status", length = 100)
	private String status;

	@ApiModelProperty(notes = "This is Email Config Id")
	@Column(name = "emailconfigid", length = 100)
	private Long emailConfigId;

	@ApiModelProperty(hidden = true)
	@Column(name = "createdon")
	@JsonProperty("createdOn")
	@JsonFormat(pattern = "yyyy-MM-dd, HH:mm:ss")
	private LocalDateTime createdOn;

	@ApiModelProperty(hidden = true)
	@Column(name = "lastmodifiedon")
	@JsonProperty("lastModifiedOn")
	@JsonFormat(pattern = "yyyy-MM-dd, HH:mm:ss")
	private LocalDateTime lastModifiedOn;

	@ManyToOne(optional = false)
	@JoinColumn(name = "event_id")
	private Event event;

	@ApiModelProperty(notes = "This is mvno id", required = true)
	@Column(name = "mvno_id", nullable = false)
	private Long mvnoId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "quotation_id")
	private QuotationDetails quotationDetails;

	@Column(name = "staff_id")
	private Long staffId;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "lead_id")
	private Long leadId;

	@Column(name = "email_content")
	private String emailContent;

	public Email() {
	}

	public Email(EmailAuditingDTO emailDTO) {
		if (emailDTO.getEmailDTOId() != null)
			this.emailId = emailDTO.getEmailDTOId();
		if (emailDTO.getSubject() != null)
			this.message = emailDTO.getSubject();
		if (emailDTO.getMvnoId() != null)
			this.mvnoId = emailDTO.getMvnoId();
		if (emailDTO.getStaffId() != null)
			this.staffId = emailDTO.getStaffId();
		if (emailDTO.getLeadId() != null)
			this.leadId = emailDTO.getLeadId();
		if (emailDTO.getBody() != null)
			this.emailContent = emailDTO.getBody();
	}
}
