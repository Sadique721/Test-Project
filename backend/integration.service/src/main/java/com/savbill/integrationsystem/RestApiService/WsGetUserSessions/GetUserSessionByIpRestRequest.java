package com.savbill.integrationsystem.RestApiService.WsGetUserSessions;

public class GetUserSessionByIpRestRequest {
    private String actionItem;
    private Long requestId;
    private String ipAddress;

    public String getActionItem() {
        return actionItem;
    }

    public void setActionItem(String actionItem) {
        this.actionItem = actionItem;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
