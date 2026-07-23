package com.savbill.integrationsystem.RestApiService.GetUserUsageSummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUserUssageSummaryResponseDto {

    private String aggregateBytesLimit;
    private String aggregateBytesRemaining;
    private Integer aggregateBytesUsed;
    private Integer inBytesLimit;
    private Integer inBytesRemaining;
    private Integer inBytesUsed;
    private Integer outBytesLimit;
    private Integer outBytesRemaining;
    private Integer outBytesUsed;
    private String packageCode;
    private String packageType;
    private Integer qodBytesLimit;
    private Integer qodBytesRemaining;
    private Integer qodBytesUsed;
    private String responseMessage;
    private String requestId;
    private Integer responseCode;
    
}
