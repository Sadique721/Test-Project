package com.savbill.notification.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.savbill.notification.helper.SmsDto;
import com.savbill.notification.helper.UpdateSmsDto;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
@Table(name = "TBLMSMS")
@ApiModel(value = "SMS Entity",description = "This is SMS entity which is used to update sms data")
public class
Sms extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated sms Id",required = true)
    @Column (name="smsid", nullable = false)
	private Long smsId;
	
	@ApiModelProperty(notes = "This is Source name of service",required = true)
    @Column (name="sourcename", length = 100)
    private String sourceName;

	@DiffIgnore
	@ApiModelProperty(notes = "This is Country Code for sms",required = true)
    @Column (name="countrycode", length = 100)
    private String countryCode;
	
	@ApiModelProperty(notes = "This is Mobile no for sms",required = true)
    @Column (name="mobileno", length = 100)
    private String mobileNo;
	
	@ApiModelProperty(notes = "This is message for sms",required = true)
    @Column (name="message")
    private String message;
	
	@ApiModelProperty(notes = "This is date for sms",required = false)
    @Column (name="date")
	@JsonProperty("date")
	@DiffIgnore
    private LocalDateTime date;
	
	@ApiModelProperty(notes = "This is status of sms",required = false)
    @Column (name="status", length = 100)
    private String status;
	
	
	@ApiModelProperty(notes = "This is SMS Config Id",required = false)
    @Column (name="smsconfigid", length = 100)
	@DiffIgnore
    private Long smsConfigId;



	//	@ManyToOne(optional = false,fetch = FetchType.LAZY)
	@Column(name = "eventid")
    private Long eventId;

	@Column(name = "eventname")
	private String eventName;
	
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
	@DiffIgnore
    private Long mvnoId;

	@ApiModelProperty(notes = "This is bu id", required = true)
	@Column (name="buid", nullable = false)
	@DiffIgnore
	private Long buId;

	@ApiModelProperty(notes = "This is remark", required = true)
	@Column (name="remark", nullable = false)
	private String remark;

	@ApiModelProperty(notes = "This is service type",allowableValues = "BSS,IWF",  value = "This field accept value only : BSS or IWF",required = true)
	@Column (name="service_type", length = 10, nullable = false)
	@DiffIgnore
	private String serviceType;

	@Column(name = "cprid")
	private Long cprId;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	public Long getSmsId() {
		return smsId;
	}
	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	
	public Long getSmsConfigId() {
		return smsConfigId;
	}
	public void setSmsConfigId(Long smsConfigId) {
		this.smsConfigId = smsConfigId;
	}
	public Long getMvnoId() {
		return mvnoId;
	}
	public void setMvnoId(Long mvnoId) {
		this.mvnoId = mvnoId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getCprId() {
		return cprId;
	}

	public void setCprId(Long cprId) {
		this.cprId = cprId;
	}

	public Sms()
	{
		super();
	}
	
	public Sms(SmsDto smsDto,Long mvnoId,Long buId)
	{
		this.sourceName = smsDto.getSourceName();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getCountryCode()))
		{
			this.countryCode = smsDto.getCountryCode();
		}
		this.mobileNo = smsDto.getMobileNo();
		this.message = smsDto.getMessage();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getStatus()))
		{
			this.status = smsDto.getStatus();
		}
		this.mvnoId = mvnoId;

	}
	
	public Sms(UpdateSmsDto smsDto,Long mvnoId, String status, String countryCode, Long buId,LocalDateTime date)
	{
		this.smsId = smsDto.getSmsId();
		this.sourceName = smsDto.getSourceName();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getCountryCode()))
		{
			this.countryCode = smsDto.getCountryCode();
		}
		else
		{
			this.countryCode = countryCode;
		}
		this.mobileNo = smsDto.getMobileNo();
		this.message = smsDto.getMessage();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getStatus()))
		{
			this.status = smsDto.getStatus();
		}
		else
		{
			this.status = status;
		}
		this.mvnoId = mvnoId;
		this.buId=buId;
		this.date=date;
	}
}

