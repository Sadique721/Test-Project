package com.diameter.dto;

import java.sql.Timestamp;

public class DiameterSessionResponse {

    private String sessionId;

    private Long applicationId;

    private String state;

    private Timestamp createDate;

    private Timestamp lastAccessDate;

    public DiameterSessionResponse() {
    }

    public DiameterSessionResponse(
            String sessionId,
            Long applicationId,
            String state,
            Timestamp createDate,
            Timestamp lastAccessDate) {

        this.sessionId = sessionId;
        this.applicationId = applicationId;
        this.state = state;
        this.createDate = createDate;
        this.lastAccessDate = lastAccessDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Timestamp lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }
}