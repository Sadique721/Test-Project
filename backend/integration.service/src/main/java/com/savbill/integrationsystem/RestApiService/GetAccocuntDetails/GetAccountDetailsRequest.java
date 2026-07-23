package com.savbill.integrationsystem.RestApiService.GetAccocuntDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountDetailsRequest {
    private String actionItem;
    private String requestId;
    private String userName;
}
