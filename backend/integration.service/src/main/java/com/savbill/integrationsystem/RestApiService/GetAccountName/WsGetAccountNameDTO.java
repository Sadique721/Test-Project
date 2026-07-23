package com.savbill.integrationsystem.RestApiService.GetAccountName;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsGetAccountNameDTO {
    private String actionItem;
    private String requestId;
    private String ipAddress;
}
