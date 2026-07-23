package com.savbill.radius.entity;

import com.savbill.radius.helper.VlanManagementDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBLMVLANMANAGEMENT")
@ApiModel(value = "VLAN Management entity",description = "This is Vlan management entity which is used to update VLAN data.")
@Data
public class VLANManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated vlan Id",required = true)
    @Column (name="VLANID", nullable = false)
    private Long vlanId;

    @ApiModelProperty(notes = "This is Ip Address of the client",required = true)
    @Column(name="VLAN_NAME", nullable = false , length = 100)
    private String vlanName;

    @ApiModelProperty(notes = "This is Ip Address of the client",required = true)
    @Column(name="NAS_TYPE", nullable = false , length = 100)
    private String nasType;

    @Column(name="CIRCUIT_TYPE", nullable = false , length = 100)
    private String circuitType;

    @Column(name="NAS_IDENTIFIER", nullable = false , length = 100)
    private String nasIdentifier;

    @Column(name="NAS_PORT_ID_1", nullable = false , length = 100)
    private String nasPortId1;

    @Column(name="NAS_PORT_ID_2", nullable = false , length = 100)
    private String nasPortId2;

    @Column(name="NAS_PORT_ID_3", nullable = false , length = 100)
    private String nasPortId3;

    @Column(name="NAS_PORT_ID_4", nullable = false , length = 100)
    private String nasPortId4;

    @Column(name="NAS_PORT_ID_5", nullable = false , length = 100)
    private String nasPortId5;

    @Column(name="CALLING_STATION_ID", nullable = false , length = 100)
    private String callingStationId;

    @Column(name="CONTEXT_NAME", nullable = false , length = 100)
    private String contextName;

    @Column(name="FILTER_ID", nullable = false , length = 100)
    private String filterId;

    @Column(name="FORWARD_POLICY", nullable = false , length = 100)
    private String forwardPolicy;

    @Column(name="HTTP_REDIRECT_PROFILE_NAME", nullable = false , length = 100)
    private String httpRedirectProfileName;

    @Column(name="RATE_LIMIT_RATE", nullable = false , length = 100)
    private String rateLimitRate;

    @Column(name="RATE_LIMIT_BURST", nullable = false , length = 100)
    private String rateLimitBurst;

    @Column(name="QOS_POLICING_POLICY_NAME", nullable = false , length = 100)
    private String qosPolicingPolicyName;

    @Column(name="QOS_METERING_POLICY_NAME", nullable = false , length = 100)
    private String qosMeteringPolicyName;

    @Column(name="PPPOE_URL", nullable = false , length = 100)
    private String pppoeUrl;

    @Column(name="PPP_DNS_PRIMARY", nullable = false , length = 100)
    private String pppDnsPrimary;

    @Column(name="PPP_DNS_SECONDARY", nullable = false , length = 100)
    private String pppDnsSecondary;

    @Column(name="PPP_NBNS_PRIMARY", nullable = false , length = 100)
    private String pppNbnsPrimary;

    @Column(name="SESSION_TIMEOUT", nullable = false , length = 100)
    private String sessionTimeOut;

    @Column(name="IDLE_TIMEOUT", nullable = false , length = 100)
    private String idleTimeOut;

    @Column(name="FRAMED_IP_ADDRESS", nullable = false , length = 100)
    private String framedIpAddress;

    @Column(name="RB_DHCP_MAX_LEASES", nullable = false , length = 100)
    private String rbDhcpMaxLeases;

    @Column(name="IP_ADDRESS_POOL_NAME", nullable = false , length = 100)
    private String ipAddressPoolName;

    @Column(name="NAT_PROFILE_NAME", nullable = false , length = 100)
    private String natProfileName;

    @Column(name="RB_INTERFACE_NAME", nullable = false , length = 100)
    private String rbInterfaceName;

    @Column(name="HTTP_REDIRECT_URL", nullable = false , length = 100)
    private String httpRedirectUrl;

    @Column(name="FRAMED_IPV6_PREFIX", nullable = false , length = 100)
    private String framedIpv6Prefix;

    @Column(name="DELEGATED_IPV6_PREFIX", nullable = false , length = 100)
    private String delegatedIpv6Prefix;

    @Column(name="FRAMED_INTERFACE_ID", nullable = false , length = 100)
    private String framedInterfaceId;

    @Column(name="FRAMED_IPV6_POOL", nullable = false , length = 100)
    private String framedIpv6Pool;

    @Column(name="IPV6_OPTION", nullable = false , length = 100)
    private String ipv6Option;

    @Column(name="IPV6_DNS", nullable = false , length = 100)
    private String ipv6Dns;

    @Column(name="DELEGATED_MAX_PREFIX", nullable = false , length = 100)
    private String delegatedMaxPrefix;

    @Column(name="DELEGATED_IPV6_POOL", nullable = false , length = 100)
    private String delegatedIpv6Pool;

    @Column(name="SUB_PROFILE", nullable = false , length = 100)
    private String subProfile;

    @Column(name="PRIORITY", nullable = false)
    private Long priority;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = VLANValidationMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "VLANID")
    private List<VLANValidationMapping> mappingList = new ArrayList<>();

    @ApiModelProperty(hidden = true)
    @Column (name="mvnoid", nullable = false, updatable = false)
    private Integer mvnoId;

    @ApiModelProperty(hidden = false)
    @Column (name="createdate")
    private Timestamp createdOn;

    @ApiModelProperty(hidden = false)
    @Column (name="lastmodificationdate")
    private Timestamp lastModifiedOn;

    @Column(name="RADIUS_ATTRIBUTE_GROUP_ID", nullable = false)
    private String RADIUS_ATTRIBUTE_GROUP_ID;

    @ApiModelProperty(hidden = false)
    @Column (name="lastauthmatched")
    private LocalDateTime lastAuthMatched;

    public VLANManagement() {
    }

    public VLANManagement(VlanManagementDto vlanManagementDto) {
        this.vlanName = vlanManagementDto.getVlanName();
        this.nasType = vlanManagementDto.getNasType();
        this.circuitType = vlanManagementDto.getCircuitType();
        this.nasIdentifier = vlanManagementDto.getNasIdentifier();
        this.nasPortId1 = vlanManagementDto.getNasPortId1();
        this.nasPortId2 = vlanManagementDto.getNasPortId2();
        this.nasPortId3 = vlanManagementDto.getNasPortId3();
        this.nasPortId4 = vlanManagementDto.getNasPortId4();
        this.nasPortId5 = vlanManagementDto.getNasPortId5();
        this.callingStationId = vlanManagementDto.getCallingStationId();
        this.contextName = vlanManagementDto.getContextName();
        this.filterId = vlanManagementDto.getFilterId();
        this.forwardPolicy = vlanManagementDto.getForwardPolicy();
        this.httpRedirectProfileName = vlanManagementDto.getHttpRedirectProfileName();
        this.rateLimitRate = vlanManagementDto.getRateLimitRate();
        this.rateLimitBurst = vlanManagementDto.getRateLimitBurst();
        this.qosPolicingPolicyName = vlanManagementDto.getQosPolicingPolicyName();
        this.qosMeteringPolicyName = vlanManagementDto.getQosMeteringPolicyName();
        this.pppoeUrl = vlanManagementDto.getPppoeUrl();
        this.pppDnsPrimary = vlanManagementDto.getPppDnsPrimary();
        this.pppDnsSecondary = vlanManagementDto.getPppDnsSecondary();
        this.pppNbnsPrimary = vlanManagementDto.getPppNbnsPrimary();
        this.sessionTimeOut = vlanManagementDto.getSessionTimeOut();
        this.idleTimeOut = vlanManagementDto.getIdleTimeOut();
        this.framedIpAddress = vlanManagementDto.getFramedIpAddress();
        this.rbDhcpMaxLeases = vlanManagementDto.getRbDhcpMaxLeases();
        this.ipAddressPoolName = vlanManagementDto.getIpAddressPoolName();
        this.natProfileName = vlanManagementDto.getNatProfileName();
        this.rbInterfaceName = vlanManagementDto.getRbInterfaceName();
        this.httpRedirectUrl = vlanManagementDto.getHttpRedirectUrl();
        this.framedIpv6Prefix = vlanManagementDto.getFramedIpv6Prefix();
        this.delegatedIpv6Prefix = vlanManagementDto.getDelegatedIpv6Prefix();
        this.framedInterfaceId = vlanManagementDto.getFramedInterfaceId();
        this.framedIpv6Pool = vlanManagementDto.getFramedIpv6Pool();
        this.ipv6Option = vlanManagementDto.getIpv6Option();
        this.ipv6Dns = vlanManagementDto.getIpv6Dns();
        this.delegatedMaxPrefix = vlanManagementDto.getDelegatedMaxPrefix();
        this.delegatedIpv6Pool = vlanManagementDto.getDelegatedIpv6Pool();
        this.subProfile = vlanManagementDto.getSubProfile();
        this.priority = vlanManagementDto.getPriority();
        this.mappingList = vlanManagementDto.getMappingList();
        this.RADIUS_ATTRIBUTE_GROUP_ID = vlanManagementDto.getRADIUS_ATTRIBUTE_GROUP_ID();
    }
}
