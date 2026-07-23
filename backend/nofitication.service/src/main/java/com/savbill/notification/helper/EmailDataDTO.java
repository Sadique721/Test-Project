package com.savbill.notification.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
public class EmailDataDTO {

    private Long emailId;


    private String sourceName;


    private String emailAddress;


    private String message;


    @JsonProperty("date")
    @JsonFormat(pattern="yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime date;


    private String status;


    private Long eventId;


    private String eventName;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getEmailId() {
        return emailId;
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


    public EmailDataDTO(Long emailId, String sourceName, String emailAddress, String message, LocalDateTime date, String status, Long eventId, String eventName) {
        this.emailId = emailId;
        this.sourceName = sourceName;
        this.emailAddress = emailAddress;
        this.message = message;
        this.date = date;
        this.status = status;
        this.eventId = eventId;
        this.eventName = eventName;
    }


}
