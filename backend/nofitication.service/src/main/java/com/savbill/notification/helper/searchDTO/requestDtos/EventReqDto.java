package com.savbill.notification.helper.searchDTO.requestDtos;

public class EventReqDto {
     private String eventName;
     private Long mvnoId;

    public EventReqDto() {
    }

    public EventReqDto(String eventName, Long mvnoId) {
        this.eventName = eventName;
        this.mvnoId = mvnoId;
    }

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
}
