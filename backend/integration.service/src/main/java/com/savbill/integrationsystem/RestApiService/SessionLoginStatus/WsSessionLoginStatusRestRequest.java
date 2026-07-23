package com.savbill.integrationsystem.RestApiService.SessionLoginStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsSessionLoginStatusRestRequest {
    protected String actionItem;
    protected Integer requestId;
    protected String ipAddress;
}
