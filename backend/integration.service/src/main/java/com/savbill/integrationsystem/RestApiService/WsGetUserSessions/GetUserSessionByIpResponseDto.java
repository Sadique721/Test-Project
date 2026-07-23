package com.savbill.integrationsystem.RestApiService.WsGetUserSessions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUserSessionByIpResponseDto {

    private String acctSessionId;
    private String callingStationId;
    private String circuitType;
    private String context;
    private String delegatedIpv6Prefixes;
    private String framedIpv6Prefixes;
    private String macAddress;
    private String medium;
    private String NASPortId;
    private Integer NASPortType;
    private String nasId;
    private Integer nasType;
    private Integer requestId;
    private Integer responseCode;
    private String responseMessage;
    private String sessionId;
    private String sessionIp;
    private String startTime;
    private String subscriberAccount;
}
