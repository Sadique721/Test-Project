package com.savbill.integrationsystem.RestApiService.UpdateUserUsage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserUsageRestRequest {
    protected String actionItem;
    protected String requestId;
    protected String userName;
    protected double usageBytes;
}
