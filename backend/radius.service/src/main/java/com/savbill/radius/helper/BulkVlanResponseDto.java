package com.savbill.radius.helper;

import lombok.Data;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class BulkVlanResponseDto {

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

        private Timestamp createdOn;

        private Timestamp lastModifiedOn;

        private String RADIUS_ATTRIBUTE_GROUP_ID;

        private LocalDateTime lastAuthMatched;

        public BulkVlanResponseDto(String vlanName, String nasType, String circuitType, String nasIdentifier, String nasPortId1,
                                   String nasPortId2, String nasPortId3, String nasPortId4, String nasPortId5, String callingStationId,
                                   String contextName, String filterId, String forwardPolicy, String httpRedirectProfileName,
                                   String rateLimitRate, String rateLimitBurst, String qosPolicingPolicyName, String qosMeteringPolicyName,
                                   String pppoeUrl, String pppDnsPrimary, String pppDnsSecondary, String pppNbnsPrimary, String sessionTimeOut,
                                   String idleTimeOut, String framedIpAddress, String rbDhcpMaxLeases, String ipAddressPoolName,
                                   String natProfileName, String rbInterfaceName, String httpRedirectUrl, String framedIpv6Prefix,
                                   String delegatedIpv6Prefix, String framedInterfaceId, String framedIpv6Pool, String ipv6Option,
                                   String ipv6Dns, String delegatedMaxPrefix, String delegatedIpv6Pool, String subProfile,
                                   Long priority, Integer mvnoId,
                                   String RADIUS_ATTRIBUTE_GROUP_ID) {
                this.vlanName = vlanName;
                this.nasType = nasType;
                this.circuitType = circuitType;
                this.nasIdentifier = nasIdentifier;
                this.nasPortId1 = nasPortId1;
                this.nasPortId2 = nasPortId2;
                this.nasPortId3 = nasPortId3;
                this.nasPortId4 = nasPortId4;
                this.nasPortId5 = nasPortId5;
                this.callingStationId = callingStationId;
                this.contextName = contextName;
                this.filterId = filterId;
                this.forwardPolicy = forwardPolicy;
                this.httpRedirectProfileName = httpRedirectProfileName;
                this.rateLimitRate = rateLimitRate;
                this.rateLimitBurst = rateLimitBurst;
                this.qosPolicingPolicyName = qosPolicingPolicyName;
                this.qosMeteringPolicyName = qosMeteringPolicyName;
                this.pppoeUrl = pppoeUrl;
                this.pppDnsPrimary = pppDnsPrimary;
                this.pppDnsSecondary = pppDnsSecondary;
                this.pppNbnsPrimary = pppNbnsPrimary;
                this.sessionTimeOut = sessionTimeOut;
                this.idleTimeOut = idleTimeOut;
                this.framedIpAddress = framedIpAddress;
                this.rbDhcpMaxLeases = rbDhcpMaxLeases;
                this.ipAddressPoolName = ipAddressPoolName;
                this.natProfileName = natProfileName;
                this.rbInterfaceName = rbInterfaceName;
                this.httpRedirectUrl = httpRedirectUrl;
                this.framedIpv6Prefix = framedIpv6Prefix;
                this.delegatedIpv6Prefix = delegatedIpv6Prefix;
                this.framedInterfaceId = framedInterfaceId;
                this.framedIpv6Pool = framedIpv6Pool;
                this.ipv6Option = ipv6Option;
                this.ipv6Dns = ipv6Dns;
                this.delegatedMaxPrefix = delegatedMaxPrefix;
                this.delegatedIpv6Pool = delegatedIpv6Pool;
                this.subProfile = subProfile;
                this.priority = priority;
                this.mvnoId = mvnoId;
                this.RADIUS_ATTRIBUTE_GROUP_ID = RADIUS_ATTRIBUTE_GROUP_ID;
        }
}
