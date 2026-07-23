package com.diameter.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiameterSessionCdr {

    private Long id;

    private String sessionId;
    private String transactionId;
    private String diameterInterface;

    private Long ccRequestNumber;

    private String serviceType;
    private String serviceContextId;

    private Long ratingGroup;
    private Long serviceIdentifier;

    private String msisdn;
    private String imsi;
    private String imei;

    private String callingParty;
    private String calledParty;

    private String ipAddress;
    private String apn;

    private String originHost;
    private String originRealm;

    private String destinationHost;
    private String destinationRealm;

    private String mediaType;
    private String sipMethod;

    private String afChargingIdentifier;
    private String flowStatus;
    private String codec;

    private String policyName;
    private String chargingRuleBaseName;

    private String qosProfile;
    private Integer qci;

    private Long uplinkBytes;
    private Long downlinkBytes;
    private Long totalBytes;

    private Long voiceSeconds;
    private Long smsCount;

    private Long usedUnits;
    private Long grantedUnits;

    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime endTime;

    private Long sessionDuration;

    private String terminationCause;
    private String terminationReason;
    private String disconnectSource;

    private Integer resultCode;
    private String resultDescription;

    private String status;
    private String errorMessage;

    private String requestPayload;
    private String responsePayload;

    private String nodeName;
    private String podName;

    private LocalDateTime createdDate;
    
    private UserLocationInfo userLocationInfo;
}