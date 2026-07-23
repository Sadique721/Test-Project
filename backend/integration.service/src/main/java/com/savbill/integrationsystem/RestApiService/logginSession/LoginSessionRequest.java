package com.savbill.integrationsystem.RestApiService.logginSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSessionRequest {
    private String actionItem;
    private String requestId;
    private String ipAddress;
    private String userName;
    private String password;
}
