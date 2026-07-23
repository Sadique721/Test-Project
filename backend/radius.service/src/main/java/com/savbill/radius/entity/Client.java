package com.savbill.radius.entity;

import com.savbill.radius.helper.ClientDto;
import com.savbill.radius.ippool.domain.IPPoolMapping;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "TBLTCLIENTS")
@ApiModel(value = "Client Entity", description = "This is Client entity which is used to update client data")
@Data
public class Client implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Client Id", required = true)
    @Column(name = "clientid", nullable = false)
    private Long clientId;

    @ApiModelProperty(notes = "This is Ip Address of the client", required = true)
    @Column(name = "clientip", nullable = false, length = 250)
    private String clientIpAddress;

    @ApiModelProperty(notes = "This is Shared key of the client", required = true)
    @Column(name = "sharedkey", nullable = false, length = 100)
    private String sharedKey;

    @ApiModelProperty(notes = "This is Time Out of the client", required = true)
    @Column(name = "timeout", nullable = false, length = 10)
    private String timeOut;

    @ApiModelProperty(notes = "This is Ip Address Type", required = true)
    @Column(name = "iptype", nullable = false, length = 15)
    private String ipType;

    @ApiModelProperty(notes = "This is Name of Client", required = false)
    @Column(name = "clientname", nullable = true, length = 100)
    private String clientname;

    @ApiModelProperty(notes = "This is Client Group Id", required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ClientGroupMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientid")
    @OrderBy("priority DESC")
    private List<ClientGroupMapping> clientGroupMappings;

    @ApiModelProperty(notes = "This is accept on IP not found", required = true)
    @Column(name = "accept_on_ip_not_found", nullable = false)
    private Boolean acceptOnIpNotFound;

    @ApiModelProperty(notes = "This is attribute to send Allocated Ip to requested client", required = true)
    @Column(name = "radius_attribute", nullable = false, length = 250)
    private String radiusAttribute;

    @ApiModelProperty(notes = "This is idle timeout to free allocated IP", required = false)
    @Column(name = "idle_timeout", length = 100)
    private Long idleTimeout;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ApiModelProperty(notes = "This is IP-Pool mapping configuration", required = true)
    @OneToMany(targetEntity = IPPoolMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clientid")
    private List<IPPoolMapping> ipPoolMappingList;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdate")
    @JsonProperty("createDate")
    private Timestamp createdOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate")
    @JsonProperty("lastModificationDate")
    private Timestamp lastModifiedOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false, updatable = false)
    private Integer mvnoId;

    @Transient
    ClientGroup clientGroupData;

    @Transient
    ClientReply clientReplyData;
    @Transient
    private String mvnoName;

    @ApiModelProperty(notes = "This is flag to set snmp profile")
    @Column(name = "snmpenable", nullable = false)
    private boolean snmpEnable;

    @ApiModelProperty(notes = "This is proxy server id")
    @JoinColumn(name = "snmpclientid")
    @OneToOne(optional = true)
    private SNMPClientProfile snmpClientProfile;

    @ApiModelProperty(notes = "This is idle timeout to free allocated IP", required = false)
    @Column(name = "session_purge_interval", length = 100)
    private Long sessionPurgeInterval;

    @ApiModelProperty(notes = "This is client group id", required = true)
    @Column(name = "deviceid", nullable = false)
    private Long deviceId;

    @ApiModelProperty(notes = "This is attribute to send Allocated Ip to requested client", required = true)
    @Column(name = "acctonattribute", nullable = false, length = 250)
    private String acctOnAttribute;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Client cloned = (Client) super.clone();
        cloned.clientGroupData = new ClientGroup();
        return cloned;
    }

    public String getMvnoName() {
        return mvnoName;
    }

    public void setMvnoName(String mvnoName) {
        this.mvnoName = mvnoName;
    }

    @ApiModelProperty(hidden = true)
    @Column(name = "vendor")
    private String vendor;


    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

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

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Timestamp lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public ClientGroup getClientGroupData() {
        return clientGroupData;
    }

    public void setClientGroupData(ClientGroup clientGroupData) {
        this.clientGroupData = clientGroupData;
    }

    public ClientReply getClientReplyData() {
        return clientReplyData;
    }

    public void setClientReplyData(ClientReply clientReplyData) {
        this.clientReplyData = clientReplyData;
    }

    public List<ClientGroupMapping> getClientGroupMappings() {
        return clientGroupMappings;
    }

    public void setClientGroupMappings(List<ClientGroupMapping> clientGroupMappings) {
        this.clientGroupMappings = clientGroupMappings;
    }

    public SNMPClientProfile getSnmpClientProfile() {
        return snmpClientProfile;
    }

    public void setSnmpClientProfile(SNMPClientProfile snmpClientProfile) {
        this.snmpClientProfile = snmpClientProfile;
    }

    public boolean isSnmpEnable() {
        return snmpEnable;
    }

    public void setSnmpEnable(boolean snmpEnable) {
        this.snmpEnable = snmpEnable;
    }

    public Boolean getAcceptOnIpNotFound() {
        return acceptOnIpNotFound;
    }

    public void setAcceptOnIpNotFound(Boolean acceptOnIpNotFound) {
        this.acceptOnIpNotFound = acceptOnIpNotFound;
    }

    public Client() {
        super();
    }

    public Client(ClientDto clientDto) {
        this.clientGroupId = clientDto.getClientGroupId();
        this.clientIpAddress = clientDto.getClientIpAddress();
        this.ipType = clientDto.getIpType();
        this.sharedKey = clientDto.getSharedKey();
        this.timeOut = clientDto.getTimeOut();
        this.clientGroupMappings = clientDto.getClientGroupMappings();
        this.acceptOnIpNotFound = clientDto.getAcceptOnIpNotFound();
        this.radiusAttribute = clientDto.getRadiusAttribute();
        this.idleTimeout = clientDto.getIdleTimeout();
        this.ipPoolMappingList = clientDto.getIpPoolMappingList();
        this.snmpEnable = clientDto.isSnmpEnable();
        this.vendor = clientDto.getVendor();
        this.sessionPurgeInterval = clientDto.getSessionPurgeInterval();
        this.acctOnAttribute = clientDto.getAcctOnAttribute();
        this.clientname=clientDto.getClientname();
        if (clientDto.isSnmpEnable()) {
            this.snmpClientProfile = clientDto.getSnmpClientProfile();
        } else {
            this.snmpClientProfile = null;
        }
    }


}
