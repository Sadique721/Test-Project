package com.savbill.integrationsystem.RestApiService.logOffUserSessions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOffUserSessions {
    private String actionItem;
    private String requestId;
    private String userName;
}
