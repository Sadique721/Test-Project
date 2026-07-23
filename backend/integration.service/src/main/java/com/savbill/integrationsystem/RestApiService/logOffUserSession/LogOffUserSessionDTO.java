package com.savbill.integrationsystem.RestApiService.logOffUserSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOffUserSessionDTO {
    private String actionItem;
    private String requestId;
    private String ipAddress;
}
