package com.savbill.notification.savbilliwfnotification.dto;

import com.savbill.notification.helper.StaffCustomDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTemplateBindingDTO {
    @ApiModelProperty(notes = "This is event Id", required = false)
    private Long eventId;
    @ApiModelProperty(notes = "This is template Id", required = false)
    private Long templateId;
    @ApiModelProperty(notes = "This is event name",required = true)
    private String eventName;
    @ApiModelProperty(notes = "This is event type",allowableValues = "Schedule,Trigger",  value = "This field accept value only : Schedule or Trigger",required = true)
    private String eventType;
    @ApiModelProperty(notes = "This is event description",required = false)
    private String description;
    @ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    private String status;
    @ApiModelProperty(notes = "This is time interval",required = false)
    private String timeInterval;
    @ApiModelProperty(notes = "This is time interval type", allowableValues = "Minutes, Hour/Hr",required = false)
    private String timeIntervalType;
    @ApiModelProperty(notes = "This is email subject",required = false)
    private String emailSubject;
    @ApiModelProperty(notes = "This is To email id",required = false)
    private String toEmailId;
    @ApiModelProperty(notes = "This is CC email id",required = false)
    private String ccEmailId;
    @ApiModelProperty(notes = "This is BCC email id",required = false)
    private String bccEmailId;
    @ApiModelProperty(notes = "This is email configuration",required = false)
    private Long emailConfigId;
    @ApiModelProperty(notes = "This is constraint type",allowableValues = "Exact Match,Regex Based",required = false)
    private String constraintType;
    @ApiModelProperty(notes = "This is column value",required = false)
    private String columnValue;
    @ApiModelProperty(notes = "This is regex",required = false)
    private String regex;
    @ApiModelProperty(notes = "This is regex group index",required = false)
    private String regexGroupIndex;
    @ApiModelProperty(notes = "This is system generated",required = false)
    private Boolean systemGenerated = false;
    @ApiModelProperty(notes = "This is template file path",required = false)
    private String templateFilePath;
    @ApiModelProperty(notes = "This is content type",required = false)
    private String contentType;
    @ApiModelProperty(notes = "This is email template",required = false)
    private Boolean isEmailTemplate = false;
    @ApiModelProperty(notes = "This is sms template",required = false)
    private Boolean isSMSTemplate = false;
    @ApiModelProperty(notes = "This is template name", required = false)
    private String templateName;
    @ApiModelProperty(notes = "This is email template data", required = false)
    private String emailTemplateData;
    @ApiModelProperty(notes = "This is sms template data", required = false)
    private String smsTemplateData;
    @ApiModelProperty(notes = "This is file name", required = false)
    private String fileName;
    @ApiModelProperty(notes = "This is content", required = false)
    private String content;
    @ApiModelProperty(notes = "This is consider frequency", required = false)
    private Boolean isFrequency = false;

    @ApiModelProperty(notes = "This is sms config ids list", required = false)
    private List<Long> smsConfigIdsList;

    @ApiModelProperty(notes = "This is staff dto list", required = false)
    private List<StaffCustomDTO> staffDtoList;

    @ApiModelProperty(notes = "This is flag to check whether append url is required or not")
    private Boolean isAppendRequired;

    @ApiModelProperty(notes = "This is appendurl")
    private String appendURL;

    public EventTemplateBindingDTO(Long eventId, Long templateId, String eventName, String eventType, String description, String status, String timeInterval, String timeIntervalType, String emailSubject, String toEmailId, String ccEmailId, String bccEmailId, Long emailConfigId, String constraintType, String columnValue, String regex, String regexGroupIndex, Boolean systemGenerated, String templateFilePath, String contentType, Boolean isEmailTemplate, Boolean isSMSTemplate, String templateName, String emailTemplateData, String smsTemplateData, String fileName, String content, Boolean isFrequency) {
        this.eventId = eventId;
        this.templateId = templateId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.description = description;
        this.status = status;
        this.timeInterval = timeInterval;
        this.timeIntervalType = timeIntervalType;
        this.emailSubject = emailSubject;
        this.toEmailId = toEmailId;
        this.ccEmailId = ccEmailId;
        this.bccEmailId = bccEmailId;
        this.emailConfigId = emailConfigId;
        this.constraintType = constraintType;
        this.columnValue = columnValue;
        this.regex = regex;
        this.regexGroupIndex = regexGroupIndex;
        this.systemGenerated = systemGenerated;
        this.templateFilePath = templateFilePath;
        this.contentType = contentType;
        this.isEmailTemplate = isEmailTemplate;
        this.isSMSTemplate = isSMSTemplate;
        this.templateName = templateName;
        this.emailTemplateData = emailTemplateData;
        this.smsTemplateData = smsTemplateData;
        this.fileName = fileName;
        this.content = content;
        this.isFrequency = isFrequency;
    }
}
