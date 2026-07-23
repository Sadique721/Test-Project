package com.savbill.notification.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class TemplatePojo {


    private Long templateId;


    private String eventName;

    private String templateName;


    private String smsTemplateData;


    private boolean smsEventConfigured;


    private String emailTemplateData;


    private boolean emailEventConfigured;


    private String status;


    private String appendUrl;

    @DiffIgnore
    private Timestamp createDate;

    @DiffIgnore
    private Timestamp lastModificationDate;


    private Integer mvnoId;


    private Long buId;



    private String buName;


    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getSmsTemplateData() {
        return smsTemplateData;
    }

    public void setSmsTemplateData(String smsTemplateData) {
        this.smsTemplateData = smsTemplateData;
    }

    public boolean isSmsEventConfigured() {
        return smsEventConfigured;
    }

    public void setSmsEventConfigured(boolean smsEventConfigured) {
        this.smsEventConfigured = smsEventConfigured;
    }

    public String getEmailTemplateData() {
        return emailTemplateData;
    }

    public void setEmailTemplateData(String emailTemplateData) {
        this.emailTemplateData = emailTemplateData;
    }

    public boolean isEmailEventConfigured() {
        return emailEventConfigured;
    }

    public void setEmailEventConfigured(boolean emailEventConfigured) {
        this.emailEventConfigured = emailEventConfigured;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppendUrl() {
        return appendUrl;
    }

    public void setAppendUrl(String appendUrl) {
        this.appendUrl = appendUrl;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Timestamp lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public TemplatePojo(Long templateId, String eventName, String templateName, String smsTemplateData, boolean smsEventConfigured, String emailTemplateData, boolean emailEventConfigured, String status, String appendUrl, Timestamp createDate, Timestamp lastModificationDate, Integer mvnoId, Long buId, String buName) {
        this.templateId = templateId;
        this.eventName = eventName;
        this.templateName = templateName;
        this.smsTemplateData = smsTemplateData;
        this.smsEventConfigured = smsEventConfigured;
        this.emailTemplateData = emailTemplateData;
        this.emailEventConfigured = emailEventConfigured;
        this.status = status;
        this.appendUrl = appendUrl;
        this.createDate = createDate;
        this.lastModificationDate = lastModificationDate;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.buName = buName;
    }

    public TemplatePojo(Long templateId,
                        String templateName,
                        String emailTemplateData,
                        String smsTemplateData,
                        String eventName,
                        boolean emailEventConfigured,
                        boolean smsEventConfigured,
                        Long buId,
                        Integer mvnoId,
                        String appendUrl,
                        String status,
                        String buName) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.emailTemplateData = emailTemplateData;
        this.smsTemplateData = smsTemplateData;
        this.eventName = eventName;
        this.emailEventConfigured = emailEventConfigured;
        this.smsEventConfigured = smsEventConfigured;
        this.buId = buId;
        this.mvnoId = mvnoId;
        this.appendUrl = appendUrl;
        this.status = status;
        this.buName = buName;
    }


    public TemplatePojo() {
    }
}
