package com.savbill.notification.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.savbill.notification.helper.EmailDto;
import com.savbill.notification.helper.UpdateEmailDto;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
@Table(name = "TBLMEMAIL")
@ApiModel(value = "Email Entity",description = "This is Email entity which is used to update email data")
public class Email extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated email Id")
    @Column (name="emailid", nullable = false)
	@DiffIgnore
	private Long emailId;
	
	@ApiModelProperty(notes = "This is Source name of service",required = true)
    @Column (name="sourcename", length = 100)
    private String sourceName;
	
	@ApiModelProperty(notes = "This is Email Address",required = true)
    @Column (name="emailaddress", length = 100)
    private String emailAddress;
	
	@ApiModelProperty(notes = "This is message for email",required = true)
    @Column (name="message", length = 1000)
    private String message;
	
	@ApiModelProperty(notes = "This is date for email",required = false)
    @Column (name="date")
	@JsonProperty("date")
	@DiffIgnore
	@JsonFormat(pattern="yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime date;
	
	@ApiModelProperty(notes = "This is status of email",required = false)
    @Column (name="status", length = 100)
    private String status;
	
	@ApiModelProperty(notes = "This is Email Config Id")
    @Column (name="emailconfigid", length = 100)
	@DiffIgnore
    private Long emailConfigId;


	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "eventid")
    private Event event;
		
	@ApiModelProperty(notes = "This is mvno id", required = true)
	@DiffIgnore
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;
	@ApiModelProperty(notes = "This is bu id", required = true)
	@DiffIgnore
	@Column (name="buid", nullable = false)
	private Long buId;

	@ApiModelProperty(notes = "This is remark", required = true)
	@Column (name="remark", nullable = false)
	private String remark;

	@Column(name="is_active")
	@DiffIgnore
	private Boolean isActive = true;
	@Column(name="is_delete")
	private Boolean isDelete = false;

	@Column (name = "email_subject", nullable = false)
	private String emailSubject;

	@Column(name = "servicetype")
	private String serviceType;

	@Column(name = "cprid")
	private Long cprId;


	@Transient
	private String eventName;

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Long getMvnoId() {
		return mvnoId;
	}
	public void setMvnoId(Long mvnoId) {
		this.mvnoId = mvnoId;
	}
	
	public Long getEmailId() {
		return emailId;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getemailaddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Long getEmailConfigId() {
		return emailConfigId;
	}
	public void setEmailConfigId(Long emailConfigId) {
		this.emailConfigId = emailConfigId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmailSubject() { return emailSubject; }

	public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }

	public String getServiceType() { return serviceType; }
	public void setServiceType(String serviceType) { this.serviceType = serviceType; }

	public Long getCprId() {
		return cprId;
	}

	public void setCprId(Long cprId) {
		this.cprId = cprId;
	}

	public Email()
	{
		super();
	}
	
	public Email(EmailDto emailDto,Long mvnoId)
	{
		this.sourceName = emailDto.getSourceName();
		this.emailAddress = emailDto.getEmailAddress();
		this.message = emailDto.getMessage();
		this.mvnoId = mvnoId;
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(emailDto.getStatus()))
		{
			this.status = emailDto.getStatus();
		}
	}
	
	public Email(UpdateEmailDto emailDto,Long mvnoId,String status,Long buId)
	{
		this.emailId = emailDto.getEmailId();
		this.sourceName = emailDto.getSourceName();
		this.emailAddress = emailDto.getEmailAddress();
		this.message = emailDto.getMessage();
		this.mvnoId = mvnoId;
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(emailDto.getStatus()))
		{
			this.status = emailDto.getStatus();
		}
		else
		{
			this.status = status;
		}
		this.buId=buId;
	}	
}
