package com.savbill.integrationsystem.RestApiService.resetUsageForAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetUsageForAccountRequest {
    private String actionItem;
    private String requestId;
    private String userName;
}
