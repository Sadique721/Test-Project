package com.savbill.radius.SoapApi.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty("knownUser")
    private boolean isKnownUser;
    private Date createdDate;
    private String createdDateString;



    public GetUserSessionresponseDto(Long cdrID,String acctSessionId, String callingStationId, String delegatedIPv6Prefix, String framedIPv6Prefix, String nasPortId, String nasPortType, String framedIpAddress, String userName,String acctSessionTime, Date createdDate) {
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
        this.createdDate = createdDate;
    }

    public GetUserSessionresponseDto(Long cdrID,String acctSessionId, String callingStationId, String delegatedIPv6Prefix, String framedIPv6Prefix, String nasPortId, String nasPortType, String framedIpAddress, String userName,String acctSessionTime) {
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

    public GetUserSessionresponseDto() {
    }
}
