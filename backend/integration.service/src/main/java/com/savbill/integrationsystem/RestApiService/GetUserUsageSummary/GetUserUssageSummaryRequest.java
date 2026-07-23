package com.savbill.integrationsystem.RestApiService.GetUserUsageSummary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserUssageSummaryRequest {

    private String actionItem;
    private Long requestId;
    private String subscriberId;
}
