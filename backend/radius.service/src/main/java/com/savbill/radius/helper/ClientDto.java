package com.savbill.radius.helper;

import com.savbill.radius.entity.ClientGroupMapping;
import com.savbill.radius.entity.SNMPClientProfile;
import com.savbill.radius.ippool.domain.IPPoolMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@ApiModel(value = "Client", description = "This is data transfer object for Client which is used to create new client")
public class ClientDto {
    @ApiModelProperty(notes = "This is Ip Address of the client", required = true)
    private String clientIpAddress;
    @ApiModelProperty(notes = "This is Shared key of the client", required = true)
    private String sharedKey;
    @ApiModelProperty(notes = "This is Time Out of the client", required = true)
    private String timeOut;
    @ApiModelProperty(notes = "This is Ip Address Type", required = true)
    private String ipType;
    @ApiModelProperty(notes = "This is Client Group Id", required = true)
    private Long clientGroupId;
    @ApiModelProperty(notes = "This is Client Group Mapping", required = true)
    private List<ClientGroupMapping> clientGroupMappings;

    @ApiModelProperty(notes = "This is accept on IP not found", required = true)
    private Boolean acceptOnIpNotFound;

    @ApiModelProperty(notes = "This is attribute to send Allocated Ip to requested client", required = true)
    private String radiusAttribute;

    @ApiModelProperty(notes = "This is idle timeout to free allocated IP", required = false)
    private Long idleTimeout;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ApiModelProperty(notes = "This is IP-Pool mapping configuration", required = true)
    private List<IPPoolMapping> ipPoolMappingList;

    @ApiModelProperty(notes = "This is attribute to enable snpm profile")
    private boolean snmpEnable;
    @ApiModelProperty(notes = "This is attribute to decide session purge interval for live users")
    private Long sessionPurgeInterval;

    private SNMPClientProfile snmpClientProfile;
    @ApiModelProperty(notes = "This is attribute to Get Vendor Details")
    private String vendor;

    @ApiModelProperty(notes = "This is id of the client", required = true)
    private Long clientId;

    private String acctOnAttribute;

    @ApiModelProperty(notes = "This is name of the client", required = false)
    private String clientname;

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public Long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(Long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public List<ClientGroupMapping> getClientGroupMappings() {
        return clientGroupMappings;
    }

    public void setClientGroupMappings(List<ClientGroupMapping> clientGroupMappings) {
        this.clientGroupMappings = clientGroupMappings;

    }

    public String getRadiusAttribute() {
        return radiusAttribute;
    }

    public void setRadiusAttribute(String radiusAttribute) {
        this.radiusAttribute = radiusAttribute;
    }

    public Long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public List<IPPoolMapping> getIpPoolMappingList() {
        return ipPoolMappingList;
    }

    public void setIpPoolMappingList(List<IPPoolMapping> ipPoolMappingList) {
        this.ipPoolMappingList = ipPoolMappingList;
    }

    public Boolean getAcceptOnIpNotFound() {
        return acceptOnIpNotFound;
    }

    public void setAcceptOnIpNotFound(Boolean acceptOnIpNotFound) {
        this.acceptOnIpNotFound = acceptOnIpNotFound;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isSnmpEnable() {
        return snmpEnable;
    }

    public void setSnmpEnable(boolean snmpEnable) {
        this.snmpEnable = snmpEnable;
    }

    public SNMPClientProfile getSnmpClientProfile() {
        return snmpClientProfile;
    }

    public void setSnmpClientProfile(SNMPClientProfile snmpClientProfile) {
        this.snmpClientProfile = snmpClientProfile;
    }

    public Long getSessionPurgeInterval() {
        return sessionPurgeInterval;
    }

    public void setSessionPurgeInterval(Long sessionPurgeInterval) {
        this.sessionPurgeInterval = sessionPurgeInterval;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getAcctOnAttribute() {
        return acctOnAttribute;
    }

    public void setAcctOnAttribute(String acctOnAttribute) {
        this.acctOnAttribute = acctOnAttribute;
    }

    public String getClientname() {
		return clientname;
	}

	public void setClientname(String clientname) {
		this.clientname = clientname;
	}

	public ClientDto() {
    }

    public ClientDto(Long clientId, String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
        this.clientId = clientId;
    }
}
