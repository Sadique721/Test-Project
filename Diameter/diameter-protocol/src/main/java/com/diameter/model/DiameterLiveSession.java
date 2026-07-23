package com.diameter.model;

import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Transient;

@Data
public class DiameterLiveSession {

    private String sessionId;

    private String transactionId;

    private String ccRequestType;

    private Long ccRequestNumber;

    private String diameterInterface;

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

    private String sessionStatus;

    private Boolean activeFlag;

    private Integer resultCode;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime lastUpdateTime;

    private String nodeName;

    private String podName;
    
    @Transient
    private String requestPayload;
    
    @Transient
    private String responsePayload;

    private String nasIpAddress;

    private String ipv4Address;

    private String ipv6Address;

    private String currentPlan;

    private LocalDateTime planStartDate;

    private LocalDateTime planExpiryDate;

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