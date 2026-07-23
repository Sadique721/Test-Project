package com.savbill.notification.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SmsDataDTO {

    //
    private Long smsId;

//
    private String sourceName;

//
    private String countryCode;

//
    private String mobileNo;

//
    private String message;

//
    @JsonProperty("date")
    @JsonFormat(pattern="yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime date;

//
    private String status;



//    private Long smsConfigId;
    private Long eventId;


    private String eventName;

//    private String eventType;


//    private Long mvnoId;
//
//
//    private Long buId;

//    private String remark;


    public SmsDataDTO(Long smsId, String sourceName, String countryCode, String mobileNo, String message, LocalDateTime date, String status, Long eventId, String eventName) {
        this.smsId = smsId;
        this.sourceName = sourceName;
        this.countryCode = countryCode;
        this.mobileNo = mobileNo;
        this.message = message;
        this.date = date;
        this.status = status;
        this.eventId = eventId;
        this.eventName = eventName;
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
}
