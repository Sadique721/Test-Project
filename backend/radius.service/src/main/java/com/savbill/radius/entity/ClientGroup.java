package com.savbill.radius.entity;

import com.savbill.radius.helper.ClientGroupDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "TBLMCLIENTGROUP")
@ApiModel(value = "Client Group Entity", description = "This is Client Group entity which is used to update client group data")
@Data
public class ClientGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Client Group Id")
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is Client Group Name")
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @ApiModelProperty(notes = "Status of the client group", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
    @Column(name = "cgstatus", nullable = false, length = 15)
    private String cgStatus;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdate")
    @JsonProperty("createDate")
    private Timestamp createdOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate")
    @JsonProperty("lastModificationDate")
    private Timestamp lastModifiedOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false)
    private Integer mvnoId;

    @OneToMany(targetEntity = ClientReply.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientgroupid", referencedColumnName = "clientgroupid")
    private List<ClientReply> clientReplyList;

    @Transient
    private List<ClientReply> clientReplyListForRejectAuthResponse;

    @Column(name = "coadmprofileid")
    private Long coaDMProfile;

    @Column(name = "dmprofileid")
    private Long DMProfile;

    @Transient
    List<ClientReply> listClientReply;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = InactiveProfileMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientgroupid")
    private List<InactiveProfileMapping> inactiveProfileMappings;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = UnknownProfileMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientgroupid")
    private List<UnknownProfileMapping> unknownProfileMappings;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = SuspendedProfileMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientgroupid")
    private List<SuspendedProfileMapping> suspendedProfileMappings;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = VlanProfileMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientgroupid")
    private List<VlanProfileMapping> vlanProfileMapping;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CoaDmProfileMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clientgroupid")
    private List<CoaDmProfileMapping> coaDmProfileMappings;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = DynamicAttributeMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clientgroupid")
    private List<DynamicAttributeMapping> dynamicAttributeMappings;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ClearCacheMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clientgroupid")
    private List<ClearCacheMapping> clearCacheMappings;

    /*
    @Column(name = "validate_nas_attribute")
    private String validateNasAttribute;
    @Column(name = "validate_nas_attribute_value")
    private String validateNasAttributeValue;
    @Column(name = "validate_ip_attribute")
    private String validateIpAttribute;
    @Column(name = "validate_ip_attribute_value")
    private String validateIpAttributeValue;
    */

    @Column(name = "permanent_disconnect_prfoile_id")
    private Long permanentDisconnectProfileId;

    @Column(name = "start_stop_attribute_value")
    private String startStopAttributeValue;

    @Column(name = "input_packet_attribute_value")
    private String inputPacketAttributeValue;

    @Transient
    private String inPutPacketAttributeType;

    @Transient
    private String inPutPacketAttributeVendorId;

    @Column(name = "output_packet_attribute_value")
    private String outputPacketAttributeValue;

    @Transient
    private String outPutPacketAttributeType;

    @Transient
    private String outPutPacketAttributeVendorId;

    @Column(name = "packet_type")
    private String packetType;

    @Column(name = "standard_attribute_checked")
    private boolean standardAttributeChecked;

    @Transient
    private String mvnoName;

    @Column(name = "authentication_profile")
    private String authenticationProfile;

    @Column(name = "customer_mac_attribute")
    private String customerMacAttribute;

    @Transient
    private List<AccessResponse> accessResponses;

    @Column(name = "vlan_check_required")
    private boolean vlanCheckRequired;

    @Column(name = "checkconcurrency")
    private boolean checkConcurrency;

    @Column(name = "logoutoldsessiononnew")
    private boolean logoutOldSessionOnNew;

    @Column(name = "dynamic_acct_session_attribute")
    private String dynamicAcctSessionAttribute;

    @Column(name = "triggercoadmonmacremove")
    private boolean triggerCOADMOnMacRemove;

    public ClientGroup() {
        super();
    }

    public ClientGroup(ClientGroupDto clientGroupDto) {
        this.cgStatus = clientGroupDto.getCgStatus();
        this.name = clientGroupDto.getName();
        this.clientReplyList = clientGroupDto.getClientReplyList();
        this.inactiveProfileMappings = clientGroupDto.getInactiveProfileMappings();
        this.unknownProfileMappings = clientGroupDto.getUnknownProfileMappings();
        this.suspendedProfileMappings = clientGroupDto.getSuspendedProfileMappings();
        this.coaDmProfileMappings = clientGroupDto.getCoaDMProfileMappings();
        this.coaDMProfile = getCoaDMProfile();
        this.coaDMProfile = getDMProfile();
        this.permanentDisconnectProfileId = clientGroupDto.getPermanentDisconnectProfileId();
        this.startStopAttributeValue = clientGroupDto.getStartStopAttributeValue();
        this.inputPacketAttributeValue = clientGroupDto.getInputPacketAttributeValue();
        this.outputPacketAttributeValue = clientGroupDto.getOutputPacketAttributeValue();
        this.packetType = clientGroupDto.getPacketType();
        this.dynamicAttributeMappings = clientGroupDto.getDynamicAttributeMappings().stream().map(attributeMappingDto -> new DynamicAttributeMapping(attributeMappingDto, getClientGroupId())).collect(Collectors.toList());
        this.standardAttributeChecked = clientGroupDto.isStandardAttributeChecked();
        this.authenticationProfile = clientGroupDto.getAuthenticationProfile();
        this.customerMacAttribute = clientGroupDto.getCustomerMacAttribute();
        this.dynamicAcctSessionAttribute = clientGroupDto.getDynamicAcctSessionAttribute();
//        this.vlanAttribute = clientGroupDto.getVlanAttribute();
        this.vlanCheckRequired = clientGroupDto.isVlanCheckRequired();
        if (clientGroupDto.getVlanProfileMapping() == null) {
            this.vlanProfileMapping = new ArrayList<>(); // Ensure it's not null
        } else {
            this.vlanProfileMapping = clientGroupDto.getVlanProfileMapping();
        }
        this.checkConcurrency = clientGroupDto.isCheckConcurrency();
        this.logoutOldSessionOnNew = clientGroupDto.isLogoutOldSessionOnNew();
        this.triggerCOADMOnMacRemove = clientGroupDto.isTriggerCOADMOnMacRemove();

        if (!CollectionUtils.isEmpty(clientGroupDto.getClearCacheMappings())) {
            List<ClearCacheMapping> cacheMappings = clientGroupDto.getClearCacheMappings();
            for (ClearCacheMapping clearCacheMapping : cacheMappings) {
                if (clearCacheMapping.getCriteria() != null) {
                    String condition = clearCacheMapping.getCriteria().toLowerCase();
                    String checkItem = "";
                    String attribute = clearCacheMapping.getAttribute();
                    String attributeValue = clearCacheMapping.getAttributeValue();
                    switch (condition) {
                        case "equal":
                            checkItem = "{" + attribute + "}=" + attributeValue;
                            break;
                        case "notequal":
                            checkItem = "{" + attribute + "}!=" + attributeValue;
                            break;
                        case "contains":
                            checkItem = "CONTAINS{REQ{" + attribute + "}," + attributeValue + "}";
                            break;
                    }
                    clearCacheMapping.setCheckitem(checkItem);
                }
            }
            this.clearCacheMappings = cacheMappings;
        }
    }


}
