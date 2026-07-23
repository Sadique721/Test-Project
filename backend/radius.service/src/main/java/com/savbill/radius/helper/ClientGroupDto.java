package com.savbill.radius.helper;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@ApiModel(value = "Client Group", description = "This is data transfer object for Client Group which is used to create new client group")
@Data
public class ClientGroupDto {
    @ApiModelProperty(notes = "This is Client Group Name")
    private String name;
    @ApiModelProperty(notes = "Status of the client group", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
    private String cgStatus;
    @OneToMany(targetEntity = ClientReply.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clientgroupid", referencedColumnName = "clientgroupid")
    private List<ClientReply> clientReplyList;
    @ApiModelProperty(notes = "This is proxy CoA/DM", allowableValues = "Both,None,CoA,DM", value = "This field accept value only : Both or sNone or CoA or DM")
    private String coadm;
    @ApiModelProperty(notes = "This is proxy CoA value")
    private String coaDMProfile;
    @ApiModelProperty(notes = "This is proxy DM value")
    private String DMProfile;
    private List<InactiveProfileMapping> inactiveProfileMappings;
    private List<UnknownProfileMapping> unknownProfileMappings;
    private List<SuspendedProfileMapping> suspendedProfileMappings;

    private List<CoaDmProfileMapping> coaDMProfileMappings;
    private List<DynamicAttributeMappingDto> dynamicAttributeMappings;
    private List<VlanProfileMapping> vlanProfileMapping;
    private List<ClearCacheMapping> clearCacheMappings;
    private String validateNasAttribute;
    private String validateNasAttributeValue;
    private String validateIpAttribute;
    private String validateIpAttributeValue;

    private Long permanentDisconnectProfileId;
    private String startStopAttributeValue;
    private String inputPacketAttributeValue;
    private String outputPacketAttributeValue;
    private String packetType;
    private boolean standardAttributeChecked;
    private String authenticationProfile;
    private String customerMacAttribute;

    private String usernameIdentityRegex;
    private String customerUserNameAttribute;

    private boolean vlanCheckRequired;
    private String vlanAttribute;
    private String vlanColumn;
    private boolean checkConcurrency;
    private boolean logoutOldSessionOnNew;
    private String dynamicAcctSessionAttribute;
    private boolean triggerCOADMOnMacRemove;
}
