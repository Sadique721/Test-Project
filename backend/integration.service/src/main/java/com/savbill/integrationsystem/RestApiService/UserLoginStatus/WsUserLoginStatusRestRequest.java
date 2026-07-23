package com.savbill.integrationsystem.RestApiService.UserLoginStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsUserLoginStatusRestRequest {

    protected String actionItem;
    protected Integer requestId;
    protected String userName;
}
