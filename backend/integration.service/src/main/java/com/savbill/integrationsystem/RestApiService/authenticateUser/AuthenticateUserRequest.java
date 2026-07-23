package com.savbill.integrationsystem.RestApiService.authenticateUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateUserRequest {
    private String actionItem;
    private String requestId;
    private String userName;
    private String password;
}
