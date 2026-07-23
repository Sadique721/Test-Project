package com.savbill.radius.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VlanCsvDto {

        @JsonProperty("VLAN_NAME")
        private String vlanName;

        @JsonProperty("NAS_TYPE")
        private String nasType;

        @JsonProperty("CIRCUIT_TYPE")
        private String circuitType;

        @JsonProperty("NAS_IDENTIFIER")
        private String nasIdentifier;

        @JsonProperty("NAS_PORT_1")
        private String nasPortId1;

        @JsonProperty("NAS_PORT_2")
        private String nasPortId2;

        @JsonProperty("NAS_PORT_3")
        private String nasPortId3;

        @JsonProperty("NAS_PORT_4")
        private String nasPortId4;

        @JsonProperty("NAS_PORT_5")
        private String nasPortId5;

        @JsonProperty("CALLING_STATION_ID")
        private String callingStationId;

        @JsonProperty("CONTEXT_NAME")
        private String contextName;

        @JsonProperty("FILTER_ID")
        private String filterId;

        @JsonProperty("FORWARD_POLICY")
        private String forwardPolicy;

        @JsonProperty("HTTP_REDIRECT_PROFILE")
        private String httpRedirectProfileName;

        @JsonProperty("RATE_LIMIT_RATE")
        private String rateLimitRate;

        @JsonProperty("RATE_LIMIT_BURST")
        private String rateLimitBurst;

        @JsonProperty("QOS_POLICING_POLICY_NAME")
        private String qosPolicingPolicyName;

        @JsonProperty("QOS_METERING_POLICY_NAME")
        private String qosMeteringPolicyName;

        @JsonProperty("PPPOE_URL")
        private String pppoeUrl;

        @JsonProperty("PPP_DNS_PRIMARY")
        private String pppDnsPrimary;

        @JsonProperty("PPP_DNS_SECONDARY")
        private String pppDnsSecondary;

        @JsonProperty("PPP_NBNS_PRIMARY")
        private String pppNbnsPrimary;

        @JsonProperty("SESSION_TIMEOUT")
        private String sessionTimeOut;

        @JsonProperty("IDLE_TIMEOUT")
        private String idleTimeOut;

        @JsonProperty("FRAMED_IP_ADDRESS")
        private String framedIpAddress;

        @JsonProperty("RB_DHCP_MAX_LEASES")
        private String rbDhcpMaxLeases;

        @JsonProperty("IP_ADDRESS_POOL_NAME")
        private String ipAddressPoolName;

        @JsonProperty("NAT_PROFILE_NAME")
        private String natProfileName;

        @JsonProperty("RB_INTERFACE_NAME")
        private String rbInterfaceName;

        @JsonProperty("HTTP_REDIRECT_URL")
        private String httpRedirectUrl;

        @JsonProperty("FRAMED_IPV6_PREFIX")
        private String framedIpv6Prefix;

        @JsonProperty("DELEGATED_IPV6_PREFIX")
        private String delegatedIpv6Prefix;

        @JsonProperty("FRAMED_INTERFACE_ID")
        private String framedInterfaceId;

        @JsonProperty("FRAMED_IPV6_POOL")
        private String framedIpv6Pool;

        @JsonProperty("IPV6_OPTION")
        private String ipv6Option;

        @JsonProperty("IPV6_DNS")
        private String ipv6Dns;

        @JsonProperty("DELEGATED_MAX_PREFIX")
        private String delegatedMaxPrefix;

        @JsonProperty("DELEGATED_IPV6_POOL")
        private String delegatedIpv6Pool;

        @JsonProperty("SUB_PROFILE")
        private String subProfile;

        @JsonProperty("PRIORITY")
        private Long priority;

        private Integer mvnoId;

        private String mappingName;

        @JsonProperty("PROFILE_ATTRIBUTE")
        private String radiusAttribute;

        @JsonProperty("REGEX")
        private String regex;

        @JsonProperty("RADIUS_ATTRIBUTE_GROUP_ID")
        private String RADIUS_ATTRIBUTE_GROUP_ID;

        @JsonProperty("CREATEDATE")
        private String createdOn;
}
