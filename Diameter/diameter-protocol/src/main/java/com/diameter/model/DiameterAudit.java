package com.diameter.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiameterAudit {

    private Long id;

    private String transactionId;
    private String sessionId;

    private String protocol;
    private Integer commandCode;
    private Long applicationId;
    private String requestType;
    private String serviceType;

    private String msisdn;
    private String imsi;
    private String imei;
    private String apn;

    private String originHost;
    private String originRealm;
    private String destinationHost;
    private String destinationRealm;

    private Integer resultCode;
    private String resultDescription;
    private String status;
    private String errorMessage;

    private Long processingTimeMs;

    private String requestPayload;
    private String responsePayload;

    private String peerName;
    private String podName;

    private LocalDateTime createdAt;

    private String ccRequestNumber;
    private String subscriptionId;
    private String framedIpAddress;
    private String framedIpv6Prefix;
    private String calledStationId;
    private String threeGppRatType;
    private String qosInformation;
    private String bearerIdentifier;
    private String ipCanType;
    private String anGwAddress;
    private String threeGppSgsnAddress;
    private String userName;
    private String originStateId;
    private String userEquipmentInfo;
    private String ccSubSessionId;
    private String tftPacketFilterInformation;
    private String chargingRuleInstall;
    private String chargingRuleRemove;
    private String defaultEpsBearerQos;
    private String supportedFeatures;
    private String eventTrigger;
    private String usageMonitoringInformation;
    private String chargingRuleReport;
    private String threeGppUserLocationInfo;
    private String terminationCause;

    private String pdpType;
    private String imsiUnauthenticatedFlag;
    private String pdpContextType;
    private String servingNodeType;
    private String chargingId;
    private String pdpAddress;
    private String ggsnAddress;
    private String dynamicAddressFlag;
    private String imsiMccMnc;
    private String nsapi;
    private String chargingCharacteristics;
    private String sgsnMccMnc;
    private String msTimeZone;
    private String userLocationInfoTime;
    
    private UserLocationInfo userLocationInfo;
}