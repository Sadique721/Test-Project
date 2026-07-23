package com.savbill.notification.helper.searchDTO.responseDtos;

import java.time.LocalDateTime;

public class EmailSearchRespDto {

    Long mvonId;


    private Long emailId;
    private String emailAddress;
    private String message;
    private LocalDateTime date;
    private String status;
    private String emailSubject;


    private String serviceType;
    private String eventName;

    private String sourceName;


    public EmailSearchRespDto() {
    }

    public EmailSearchRespDto(Long mvonId,Long emailId, String emailAddress, String message, LocalDateTime date, String status, String emailSubject, String serviceType, String eventName, String sourceName) {
        this.mvonId = mvonId;
        this.emailId = emailId;
        this.emailAddress = emailAddress;
        this.message = message;
        this.date = date;
        this.status = status;
        this.emailSubject = emailSubject;
        this.serviceType = serviceType;
        this.eventName = eventName;
        this.sourceName = sourceName;
    }
    public Long getMvonId() {
        return mvonId;
    }

    public void setMvonId(Long mvonId) {
        this.mvonId = mvonId;
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getEmailAddress() {
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

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
