package com.savbill.radius.helper;

import com.savbill.radius.entity.AuthModeAttributeMapping;
import com.savbill.radius.entity.ProfileMapping;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "Radius Profile", description = "This is data transfer object for Radius Profile which is used to create Radius Profile")
public class RadiusProfileDto {
    @ApiModelProperty(notes = "Name of the radius profile")
    private String name;
    @ApiModelProperty(notes = "Status of the radius profile", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive")
    private String status;
    @ApiModelProperty(notes = "Check Item of the radius profile")
    private String checkItem;
    @ApiModelProperty(notes = "Account cdr status of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    private String accountCdrStatus;
    @ApiModelProperty(notes = "Session status of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    private String sessionStatus;
    @ApiModelProperty(notes = "This is mapping master")
    private String mappingMaster;
    @ApiModelProperty(notes = "This is priority of the radius profile")
    private Long priority;
    @ApiModelProperty(notes = "This is request type of the radius profile", allowableValues = "Authentication,Accounting", value = "This field accept value only : Authentication or Accounting")
    private String requestType;
    @ApiModelProperty(notes = "This is auth audit of the radius profile", allowableValues = "Enable,Disable", value = "This field accept value only : Enable or Disable")
    private String authAudit;
    @ApiModelProperty(notes = "This is proxy server name")
    private String proxyServerName;

    @ApiModelProperty(notes = "This is device Driver name")
    private String deviceDriverName;
    //    @ApiModelProperty(notes = "This is proxy CoA/DM", allowableValues = "None,CoA,DM", value = "This field accept value only : None or CoA or DM")
//    private String coadm;
//    @ApiModelProperty(notes = "This is proxy CoA/DM value")
//    private String coaDMProfile;
    @ApiModelProperty(notes = "This is autoProvisionMac value")
    private String autoProvisionMac;

    @ApiModelProperty(notes = "This is authentication mode")
    private String authenticationMode;

    @ApiModelProperty(notes = "This is authentication type")
    private String authenticationType;

    @ApiModelProperty(notes = "This is authentication type")
    private String authenticationSubType;

    @ApiModelProperty(notes = "This is trust certificate path")
    private String trustStoreDoc;

    @ApiModelProperty(notes = "This is trust mvnoName")
    private String mvnoName;

    @ApiModelProperty(notes = "This is keystoreDoc")
    private String keystoreDoc;

    @ApiModelProperty(notes = "This is trustStorePassworde")
    private String trustStorePassword;

    @ApiModelProperty(notes = "This is keystorePassword")
    private String keystorePassword;

    @ApiModelProperty(notes = "This is Password Check Flag")
    @JsonProperty("passwordCheckRequired")
    private boolean passwordCheckRequired;


    @ApiModelProperty(notes = "This is User name identity regex")
    @JsonProperty("usernameIdentityRegex")
    private String usernameIdentityRegex;
    @ApiModelProperty(notes = "This is User name Attribute key")
    @JsonProperty("customerUserNameAttribute")
    private String customerUserNameAttribute;

    @ApiModelProperty(notes = "This is flag to delete Live session if same mac get in request")
    @JsonProperty("terminateSessionOnDuplicateMac")
    private boolean terminateSessionOnDuplicateMac;

    @ApiModelProperty(notes = "This is flag to delete Live session if same mac get in request")
    @JsonProperty("addLiveSessionOnInterim")
    private boolean addLiveSessionOnInterim;

    @ApiModelProperty(notes = "This is flag to disconnect session on interim")
    @JsonProperty("disconnectSessionOnInterim")
    private boolean disconnectSessionOnInterim;

//    @ApiModelProperty(notes = "This is Mac Attribute key")
//    @JsonProperty("customerMacAttribute")
//    private String customerMacAttribute;


    private List<AuthModeAttributeMapping> authModeAttributeMappings;

    private List<ProfileMapping> profileMappings;

    public List<ProfileMapping> getProfileMappings() {
        return profileMappings;
    }

    public void setProfileMappings(List<ProfileMapping> profileMappings) {
        this.profileMappings = profileMappings;
    }

    public String getAutoProvisionMac() {
        return autoProvisionMac;
    }

    public void setAutoProvisionMac(String autoProvisionMac) {
        this.autoProvisionMac = autoProvisionMac;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMappingMaster() {
        return mappingMaster;
    }

    public void setMappingMaster(String mappingMaster) {
        this.mappingMaster = mappingMaster;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
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

    public String getProxyServerName() {
        return proxyServerName;
    }

    public void setProxyServerName(String proxyServerName) {
        this.proxyServerName = proxyServerName;
    }

    public String getDeviceDriverName() {
        return deviceDriverName;
    }

    public void setDeviceDriverName(String deviceDriverName) {
        this.deviceDriverName = deviceDriverName;
    }

    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(String authenticationMode) {
        this.authenticationMode = authenticationMode;
    }

    public List<AuthModeAttributeMapping> getAuthModeAttributeMappings() {
        return authModeAttributeMappings;
    }

    public void setAuthModeAttributeMappings(List<AuthModeAttributeMapping> authModeAttributeMappings) {
        this.authModeAttributeMappings = authModeAttributeMappings;
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

    public String getTrustStoreDoc() {

        return trustStoreDoc;
    }

    public void setTrustStoreDoc(String trustStoreDoc) {
        this.trustStoreDoc = trustStoreDoc;
    }

    public String getMvnoName() {
        return mvnoName;
    }

    public void setMvnoName(String mvnoName) {
        this.mvnoName = mvnoName;
    }

    public String getKeystoreDoc() {
        return keystoreDoc;
    }

    public void setKeystoreDoc(String keystoreDoc) {
        this.keystoreDoc = keystoreDoc;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeystorePassword() {
        return keystorePassword;
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

//    public String getCustomerMacAttribute() {
//        return customerMacAttribute;
//    }
//
//    public void setCustomerMacAttribute(String customerMacAttribute) {
//        this.customerMacAttribute = customerMacAttribute;
//    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
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

    public boolean isDisconnectSessionOnInterim() {
        return disconnectSessionOnInterim;
    }

    public void setDisconnectSessionOnInterim(boolean disconnectSessionOnInterim) {
        this.disconnectSessionOnInterim = disconnectSessionOnInterim;
    }
}
