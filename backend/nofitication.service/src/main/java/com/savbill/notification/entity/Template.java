package com.savbill.notification.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import com.savbill.notification.helper.TemplateDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
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
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventid", nullable = false)
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

	@DiffIgnore
	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	private Timestamp createDate;

	@DiffIgnore
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	private Timestamp lastModificationDate;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid")
	private Integer mvnoId;

	@ApiModelProperty(hidden = true)
	@Column (name="buid")
	private Long buId;

	@Column(name="is_active")
	private Boolean isActive = true;

	@Column(name="is_delete")
	private Boolean isDelete = false;

	@Column(name = "templatefilepath")
	private String templateFilePath;

	@Column(name = "contenttype")
	private String contentType;

	@Column(name = "isemailtemplate")
	private Boolean isEmailTemplate = false;

	@Column(name = "issmstemplate")
	private Boolean isSMSTemplate = false;

	@Column(name = "servicetype")
	private String serviceType;

	@Column(name = "filename")
	private String fileName;

	@Column(name = "content")
	private String content;

	@Transient
	private String buName;

	@Column (name="is_append_required")
	private Boolean isAppendRequired = false;
	
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
		this.isAppendRequired=templateDto.getIsAppendRequired();
	}
}
