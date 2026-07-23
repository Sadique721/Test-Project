package com.savbill.integrationsystem.SOAPService.GetUserUsageSummary;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserSessionresponseDto {
    private Long cdrID;
    private String acctSessionId;
    private String callingStationId;
    private String delegatedIPv6Prefix;
    private String framedIPv6Prefix;
    private String nasPortId;
    private String nasPortType;
    private String framedIpAddress;
    private String userName;
    private String acctSessionTime;
    private boolean isKnownUser;
    private String createdDate;
    private String createdDateString;



    public GetUserSessionresponseDto(Long cdrID,String acctSessionId, String callingStationId, String delegatedIPv6Prefix, String framedIPv6Prefix, String nasPortId, String nasPortType, String framedIpAddress, String userName, String acctSessionTime) {
        this.cdrID = cdrID;
        this.acctSessionId = acctSessionId;
        this.callingStationId = callingStationId;
        this.delegatedIPv6Prefix = delegatedIPv6Prefix;
        this.framedIPv6Prefix = framedIPv6Prefix;
        this.nasPortId = nasPortId;
        this.nasPortType = nasPortType;
        this.framedIpAddress = framedIpAddress;
        this.userName = userName;
        this.acctSessionTime = acctSessionTime;
    }
}
