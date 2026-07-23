package com.savbill.radius.entity;

import com.savbill.radius.aaa.eap.util.KeyStoreImpl;
import com.savbill.radius.helper.RadiusProfileDto;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "TBLMRADIUSPROFILE")
@ApiModel(value = "Radius Profile Entity", description = "This is Radius Profile entity which is used to update radius profile data")
public class RadiusProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated radius profile id")
    @Column(name = "radiusprofileid", nullable = false)
    private Long radiusProfileId;

    @ApiModelProperty(notes = "Name of the radius profile")
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @ApiModelProperty(notes = "Status of the radius profile", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive")
    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @ApiModelProperty(notes = "Check Item of the radius profile")
    @Column(name = "checkitem", nullable = false, length = 250)
    private String checkItem;

    @ApiModelProperty(notes = "Account cdr status of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    @Column(name = "accountcdrstatus", length = 10)
    private String accountCdrStatus;

    @ApiModelProperty(notes = "Session status of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    @Column(name = "sessionstatus", length = 10)
    private String sessionStatus;

    @ApiModelProperty(notes = "This is priority of the radius profile")
    @Column(name = "priority", nullable = false)
    private Long priority;

    @ApiModelProperty(notes = "This is request type of the radius profile", allowableValues = "Authentication,Accounting", value = "This field accept value only : Authentication or Accounting")
    @Column(name = "requesttype", nullable = false, length = 20)
    private String requestType;

    @ApiModelProperty(notes = "This is auth audit of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    @Column(name = "authaudit", length = 10)
    private String authAudit;

    @ApiModelProperty(notes = "This is proxy server id")
    @JoinColumn(name = "proxyserverid")
    @OneToOne(optional = false)
    private ProxyServer proxyServer;

//    @ApiModelProperty(notes = "This is proxy CoA/DM", allowableValues = "None,CoA,DM", value = "This field accept value only : None or CoA or DM")
//    @Column(name = "coadm", nullable = false)
//    private String coadm;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdate")
    @JsonProperty("createDate")
    private Timestamp createdOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate")
    @JsonProperty("lastModificationDate")
    private Timestamp lastModifiedOn;

//    @ApiModelProperty(notes = "This is CoA profile id")
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "coadmprofileid")
//    private CoaDMProfile coaDMProfile;

    @ApiModelProperty(notes = "This is mapping master id")
    @ManyToOne
    @JoinColumn(name = "mappingmasterid")
    private DBMappingMaster mappingMaster;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false, updatable = false)
    private Integer mvnoId;

    @ApiModelProperty(notes = "This is autoProvisionMac")
    @Column(name = "auto_provision_mac")
    private String autoProvisionMac;

    @ApiModelProperty(notes = "This is device Driver name")
    @Column(name = "device_driver_name")
    private String deviceDriverName;

    @ApiModelProperty(notes = "This is authentication mode")
    @Column(name = "authentication_mode")
    private String authenticationMode;

    @ApiModelProperty(notes = "This is authentication type")
    @Column(name = "authentication_type")
    private String authenticationType;

    @ApiModelProperty(notes = "This is authentication sub type")
    @Column(name = "authentication_sub_type")
    private String authenticationSubType;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = AuthModeAttributeMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "radiusprofileid")

    private List<AuthModeAttributeMapping> authModeAttributeMappings;

    @Transient
    KeyStoreImpl keyStore;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ProfileMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "PROFILEID")

    private List<ProfileMapping> profileMappings;

    @Column(name = "ispasswordcheckrequired")
    private boolean passwordCheckRequired;

    @Column(name = "username_identity_regex")
    private String usernameIdentityRegex;

    @Column(name = "customer_username_attribute")
    private String customerUserNameAttribute;

    @Column(name = "terminatesessiononduplicatemac")
    private boolean terminateSessionOnDuplicateMac;

    @Column(name = "addlivesessiononinterim")
    private boolean addLiveSessionOnInterim;

    @Column(name = "disconnectsessiononinterim")
    private boolean disconnectSessionOnInterim;

    @Transient
    private transient ConcurrentMap dbFieldMapping = new ConcurrentHashMap();

//    @Column(name = "customer_mac_attribute")
//    private String customerMacAttribute;

    public String getDeviceDriverName() {
        return deviceDriverName;
    }

    public void setDeviceDriverName(String deviceDriverName) {
        this.deviceDriverName = deviceDriverName;
    }

    public String getAutoProvisionMac() {
        return autoProvisionMac;
    }

    public void setAutoProvisionMac(String autoProvisionMac) {
        this.autoProvisionMac = autoProvisionMac;
    }


    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(String checkItem) {
        this.checkItem = checkItem;
    }

    public String getAccountCdrStatus() {
        return accountCdrStatus;
    }

    public void setAccountCdrStatus(String accountCdrStatus) {
        this.accountCdrStatus = accountCdrStatus;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public DBMappingMaster getMappingMaster() {
        return mappingMaster;
    }

    public void setMappingMaster(DBMappingMaster mappingMaster) {
        this.mappingMaster = mappingMaster;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
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

    public Long getRadiusProfileId() {
        return radiusProfileId;
    }

    public void setRadiusProfileId(Long radiusProfileId) {
        this.radiusProfileId = radiusProfileId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getAuthAudit() {
        return authAudit;
    }

    public void setAuthAudit(String authAudit) {
        this.authAudit = authAudit;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    // public String getCoadm() {
//	return coadm;
//    }

//    public void setCoadm(String coadm) {
//	this.coadm = coadm;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(String authenticationMode) {
        this.authenticationMode = authenticationMode;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationSubType() {
        return authenticationSubType;
    }

    public void setAuthenticationSubType(String authenticationSubType) {
        this.authenticationSubType = authenticationSubType;
    }

    public List<AuthModeAttributeMapping> getAuthModeAttributeMappings() {
        return authModeAttributeMappings;
    }

    public void setAuthModeAttributeMappings(List<AuthModeAttributeMapping> authModeAttributeMappings) {
        this.authModeAttributeMappings = authModeAttributeMappings;
    }

    public KeyStoreImpl getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStoreImpl keyStore) {
        this.keyStore = keyStore;
    }

    //    public CoaDMProfile getCoaDMProfile() {
    public List<ProfileMapping> getProfileMappings() {
        return profileMappings;
    }

    public void setProfileMappings(List<ProfileMapping> profileMappings) {
        this.profileMappings = profileMappings;
    }

    public boolean isPasswordCheckRequired() {
        return passwordCheckRequired;
    }

    public void setPasswordCheckRequired(boolean passwordCheckRequired) {
        this.passwordCheckRequired = passwordCheckRequired;
    }

    public String getUsernameIdentityRegex() {
        return usernameIdentityRegex;
    }

    public void setUsernameIdentityRegex(String usernameIdentityRegex) {
        this.usernameIdentityRegex = usernameIdentityRegex;
    }

    public String getCustomerUserNameAttribute() {
        return customerUserNameAttribute;
    }

    public void setCustomerUserNameAttribute(String customerUserNameAttribute) {
        this.customerUserNameAttribute = customerUserNameAttribute;
    }

    public boolean isTerminateSessionOnDuplicateMac() {
        return terminateSessionOnDuplicateMac;
    }

    public void setTerminateSessionOnDuplicateMac(boolean terminateSessionOnDuplicateMac) {
        this.terminateSessionOnDuplicateMac = terminateSessionOnDuplicateMac;
    }

    public boolean isAddLiveSessionOnInterim() {
        return addLiveSessionOnInterim;
    }

    public void setAddLiveSessionOnInterim(boolean addLiveSessionOnInterim) {
        this.addLiveSessionOnInterim = addLiveSessionOnInterim;
    }

    public ConcurrentMap getDbFieldMapping() {
        return dbFieldMapping;
    }

    public void setDbFieldMapping(ConcurrentMap dbFieldMapping) {
        this.dbFieldMapping = dbFieldMapping;
    }

    public boolean isDisconnectSessionOnInterim() {
        return disconnectSessionOnInterim;
    }

    public void setDisconnectSessionOnInterim(boolean disconnectSessionOnInterim) {
        this.disconnectSessionOnInterim = disconnectSessionOnInterim;
    }

    public RadiusProfile() {
        super();
    }

    public RadiusProfile(RadiusProfileDto radiusProfileDto, DBMappingMaster mappingMaster) {
        this.name = radiusProfileDto.getName();
        this.status = radiusProfileDto.getStatus();
        this.checkItem = radiusProfileDto.getCheckItem();
        this.accountCdrStatus = radiusProfileDto.getAccountCdrStatus();
        this.sessionStatus = radiusProfileDto.getSessionStatus();
        this.mappingMaster = mappingMaster;
        this.priority = radiusProfileDto.getPriority();
        this.requestType = radiusProfileDto.getRequestType();
        this.authAudit = radiusProfileDto.getAuthAudit();
        this.authenticationMode = radiusProfileDto.getAuthenticationMode();
        this.authenticationType = radiusProfileDto.getAuthenticationType();
        this.authenticationSubType = radiusProfileDto.getAuthenticationSubType();
        this.passwordCheckRequired = radiusProfileDto.isPasswordCheckRequired();
        this.usernameIdentityRegex = radiusProfileDto.getUsernameIdentityRegex();
        if (radiusProfileDto.getCustomerUserNameAttribute() != null)
            this.customerUserNameAttribute = radiusProfileDto.getCustomerUserNameAttribute();
        else
            this.customerUserNameAttribute = "User-Name";
        this.terminateSessionOnDuplicateMac = radiusProfileDto.isTerminateSessionOnDuplicateMac();
        this.addLiveSessionOnInterim = radiusProfileDto.isAddLiveSessionOnInterim();
    }

    public RadiusProfile(RadiusProfileDto radiusProfileDto, ProxyServer proxyServer,
                         DBMappingMaster mappingMaster) {
        this.name = radiusProfileDto.getName();
        this.status = radiusProfileDto.getStatus();
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(radiusProfileDto.getCheckItem())) {
            this.checkItem = null;
        } else {
            this.checkItem = radiusProfileDto.getCheckItem();
        }
        this.accountCdrStatus = radiusProfileDto.getAccountCdrStatus();
        this.sessionStatus = radiusProfileDto.getSessionStatus();
        this.mappingMaster = mappingMaster;
        this.priority = radiusProfileDto.getPriority();
        this.requestType = radiusProfileDto.getRequestType();
        this.authAudit = radiusProfileDto.getAuthAudit();
        if (radiusProfileDto.getDeviceDriverName() != null) {
            this.deviceDriverName = radiusProfileDto.getDeviceDriverName();
        }
        if (proxyServer != null) {
            this.proxyServer = proxyServer;
        }

        this.autoProvisionMac = radiusProfileDto.getAutoProvisionMac();
        this.authenticationMode = radiusProfileDto.getAuthenticationMode();
        this.authenticationType = radiusProfileDto.getAuthenticationType();
        this.authenticationSubType = radiusProfileDto.getAuthenticationSubType();
        if (!CollectionUtils.isEmpty(radiusProfileDto.getAuthModeAttributeMappings())) {
            this.authModeAttributeMappings = radiusProfileDto.getAuthModeAttributeMappings();
        }
        this.passwordCheckRequired = radiusProfileDto.isPasswordCheckRequired();
        this.usernameIdentityRegex = radiusProfileDto.getUsernameIdentityRegex();
        if (radiusProfileDto.getCustomerUserNameAttribute() != null)
            this.customerUserNameAttribute = radiusProfileDto.getCustomerUserNameAttribute();
        else
            this.customerUserNameAttribute = "User-Name";
        this.terminateSessionOnDuplicateMac = radiusProfileDto.isTerminateSessionOnDuplicateMac();
        this.addLiveSessionOnInterim = radiusProfileDto.isAddLiveSessionOnInterim();
        this.disconnectSessionOnInterim = radiusProfileDto.isDisconnectSessionOnInterim();
    }

}
