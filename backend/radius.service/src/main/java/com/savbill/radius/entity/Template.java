package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.savbill.radius.helper.TemplateDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.querydsl.core.annotations.QueryInit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMTEMPLATE")
@ApiModel(value = "Template Entity",description = "This is template entity")
public class Template 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated template id")
    @Column (name="templateid", nullable = false)
	private Long templateId;
	
	@JsonBackReference
	@OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventid", nullable = false)
	@QueryInit("eventId")
    private Event event;
	
	@ApiModelProperty(notes = "This is template name",required = true)
    @Column (name="templatename", length = 250, nullable = false)
    private String templateName;
	
	@ApiModelProperty(notes = "This is sms template data",required = true)
    @Column (name="smstemplatedata",nullable = false)
    private String smsTemplateData;
	
	@ApiModelProperty(notes = "This is flag to check whether event is configured for sms or not",required = true)
    @Column (name="smseventconfigured",nullable = false)
    private boolean smsEventConfigured;
	
	@ApiModelProperty(notes = "This is email template data",required = true)
    @Column (name="emailtemplatedata",nullable = false)
    private String emailTemplateData;
	
	@ApiModelProperty(notes = "This is flag to check whether event is configured for email or not",required = true)
    @Column (name="emaileventconfigured",nullable = false)
    private boolean emailEventConfigured;
	
	@ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    @Column (name="status", nullable = false , length = 10)
    private String status;
	@ApiModelProperty(notes = "This is Gateway Template ID",required = false)
    @Column (name="appendurl")
    private String appendUrl;
	
	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	private Timestamp createDate;
	
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	private Timestamp lastModificationDate;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Template(TemplateDto templateDto, Event event) 
	{
		this.emailEventConfigured=templateDto.isEmailEventConfigured();
		this.smsEventConfigured=templateDto.isSmsEventConfigured();
		this.appendUrl=templateDto.getAppendUrl();
		this.status=templateDto.getStatus();
		this.smsTemplateData=templateDto.getSmsTemplateData();
		this.emailTemplateData=templateDto.getEmailTemplateData();
		this.templateName=templateDto.getTemplateName();
		this.event = event;
	}
}
