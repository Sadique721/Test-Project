package com.savbill.radius.helper;

import com.savbill.radius.entity.VLANValidationMapping;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VlanManagementDto {

        private Long vlanId;
        private String vlanName;
        private String nasType;
        private String circuitType;
        private String nasIdentifier;
        private String nasPortId1;
        private String nasPortId2;
        private String nasPortId3;
        private String nasPortId4;
        private String nasPortId5;
        private String callingStationId;
        private String contextName;
        private String filterId;
        private String forwardPolicy;
        private String httpRedirectProfileName;
        private String rateLimitRate;
        private String rateLimitBurst;
        private String qosPolicingPolicyName;
        private String qosMeteringPolicyName;
        private String pppoeUrl;
        private String pppDnsPrimary;
        private String pppDnsSecondary;
        private String pppNbnsPrimary;
        private String sessionTimeOut;
        private String idleTimeOut;
        private String framedIpAddress;
        private String rbDhcpMaxLeases;
        private String ipAddressPoolName;
        private String natProfileName;
        private String rbInterfaceName;
        private String httpRedirectUrl;
        private String framedIpv6Prefix;
        private String delegatedIpv6Prefix;
        private String framedInterfaceId;
        private String framedIpv6Pool;
        private String ipv6Option;
        private String ipv6Dns;
        private String delegatedMaxPrefix;
        private String delegatedIpv6Pool;
        private String subProfile;
        private Long priority;
        private Integer mvnoId;
        private List<VLANValidationMapping> mappingList;
        private String loggedInUser;
        private Integer staffId;
        private String RADIUS_ATTRIBUTE_GROUP_ID;
        private LocalDateTime lastAuthMatched;
}
