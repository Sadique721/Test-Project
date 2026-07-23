package com.diameter.model;

import com.diameter.util.BooleanToShortDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tblcustomers")
@Data
public class Customer {
	@Id
    private BigInteger custId;
	@NotBlank
    private String userName;
	@NotBlank
    private String password;
	@NotBlank
    private String firstName;
	@NotBlank
    private String lastName;
	@Email
	@NotBlank
    private String email;
    private String cStatus;
    private Timestamp lastLoginTime;
    private Integer failCount;
    private LocalDateTime lastPasswordChange;
    private String accountNumber;
    private String accountType;
    private Timestamp birthDate;
    private String country;
    private String cui;
    @NotBlank
    private String customerType;
    private String gender;
    private String imsi;
    private String phone;
    private String subscriberPackage;
    private String subscriberPackageId;
    private Timestamp createDate;
    private String expiryDate;
    private LocalDateTime lastStatusChangeDate;
    private LocalDate nextBillDate;
    private LocalDate lastBillDate;
    private BigDecimal billDay;
    private BigDecimal outstandingBalance;
    private BigInteger partnerId;
    private String asnNumber;
    private String bngRouterInterface;
    private String bngRouterName;
    private String ipPrefixes;
    private String ipv6Prefixes;
    private String lanIp;
    private String lanIpv6;
    private String llAccountId;
    private String llConnectionType;
    private String llExpiryDate;
    private String llMedium;
    private String llServiceId;
    private String macAddress;
    private String peerIp;
    private String poolIp;
    private String qos;
    private String rdExport;
    private String rdValue;
    private String vlanId;
    private String vrfName;
    private String vsiId;
    private String vsiName;
    private String wanIp;
    private String wanIpv6;
    private String billEntityName;
    private String purchaseOrder;
    private String remarks;
    private String addParam1;
    private String addParam2;
    private String addParam3;
    private String addParam4;
    private Integer parentCustId;
    private String invoiceOption;
    private String oldPassword1;
    private String oldPassword2;
    private String oldPassword3;
    private LocalDateTime firstActivationDate;
    private String allowedIpAddrs;
    private String selfCarePwd;
    private String title;
    @NotBlank
    private String custName;
    private String contactPerson;
    private String cafNo;
    private String pan;
    private String gst;
    private String aadhar;
    private Short macTelFlag;
    private String mobile;
    private String altMobile;
    private String altPhone;
    private String altEmail;
    private String fax;
    private BigInteger resellerId;
    private BigInteger salesRepId;
    private BigDecimal deposit;
    private String voiceSrvType;
    private String didNo;
    private String childDidNo;
    private String intercomNo;
    private String intercomGrp;
    private Short onlineRenewalFlag;
    private Short voipEnableFlag;
    private String custCategory;
    private BigDecimal walletBalance;
    private String networkType;
    private BigInteger oltSlotId;
    private BigInteger oltPortId;
    private String strConnType;
    private String strOltName;
    private String strSlotName;
    private String strPortName;
    private Integer createdByStaffId;
    private Integer lastModifiedByStaffId;
    private BigInteger serviceAreaId;
    private BigInteger networkDeviceId;
    private String onuId;
    @JsonDeserialize(using = BooleanToShortDeserializer.class)
    private Short isDeleted;
    private BigInteger defaultPoolId;
    private String otp;
    private LocalDateTime otpValidate;
    private String createByName;
    private String updateByName;
    private Timestamp lastModifiedDate;
    private String latitude;
    private String longitude;
    private String url;
    private String gisCode;
    private Short voiceProvision;
    private String salesRemark;
    private String serviceType;
    private BigInteger previousCafApprover;
    private BigInteger nextCafApprover;
    private String cafApproveStatus;
    @NotNull
    private BigInteger mvnoId;
    private String calendarType;
    private String invoiceType;
    private String nasPort;
    private String framedIp;
    private String framedIpBind;
    private String ipPoolNameBind;
    private BigInteger buid;
    private BigInteger maxConcurrentSession;
    private String gatewayIp;
    private String skipNetConf;
    private String rdImport;
    private Short mvnoDeactivationFlag;
    private String ipv4;
    private String ipv6;
    private String vlan;
    private String nasPortId;
    private String nasIpAddress;
    private String framedIpv6Address;
    private String delegatedPrefix;
    private String framedRoute;
    private Short macProvision;
    private Short macAuthEnable;
    private String framedIpNetmask;
    private String framedIpv6Prefix;
    private String primaryDns;
    private String primaryIpv6Dns;
    private String secondaryIpv6Dns;
    private String secondaryDns;
    private BigInteger macRetentionPeriod;
    private String macRetentionUnit;
    private LocalDate nextQuotaResetDate;
    private String blockNo;
    @Valid
    @OneToMany(mappedBy = "custId",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CustomerQuota> quotas;
    private Boolean isinvoicestop = false;
    
	@Override
	public String toString() {
		return "Customer [custId=" + custId + ", userName=" + userName + ", password=" + password + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email + ", cStatus=" + cStatus
				+ ", lastLoginTime=" + lastLoginTime + ", failCount=" + failCount + ", lastPasswordChange="
				+ lastPasswordChange + ", accountNumber=" + accountNumber + ", accountType=" + accountType
				+ ", birthDate=" + birthDate + ", country=" + country + ", cui=" + cui + ", customerType="
				+ customerType + ", gender=" + gender + ", imsi=" + imsi + ", phone=" + phone + ", subscriberPackage="
				+ subscriberPackage + ", subscriberPackageId=" + subscriberPackageId + ", createDate=" + createDate
				+ ", expiryDate=" + expiryDate + ", lastStatusChangeDate=" + lastStatusChangeDate + ", nextBillDate="
				+ nextBillDate + ", lastBillDate=" + lastBillDate + ", billDay=" + billDay + ", outstandingBalance="
				+ outstandingBalance + ", partnerId=" + partnerId + ", asnNumber=" + asnNumber + ", bngRouterInterface="
				+ bngRouterInterface + ", bngRouterName=" + bngRouterName + ", ipPrefixes=" + ipPrefixes
				+ ", ipv6Prefixes=" + ipv6Prefixes + ", lanIp=" + lanIp + ", lanIpv6=" + lanIpv6 + ", llAccountId="
				+ llAccountId + ", llConnectionType=" + llConnectionType + ", llExpiryDate=" + llExpiryDate
				+ ", llMedium=" + llMedium + ", llServiceId=" + llServiceId + ", macAddress=" + macAddress + ", peerIp="
				+ peerIp + ", poolIp=" + poolIp + ", qos=" + qos + ", rdExport=" + rdExport + ", rdValue=" + rdValue
				+ ", vlanId=" + vlanId + ", vrfName=" + vrfName + ", vsiId=" + vsiId + ", vsiName=" + vsiName
				+ ", wanIp=" + wanIp + ", wanIpv6=" + wanIpv6 + ", billEntityName=" + billEntityName
				+ ", purchaseOrder=" + purchaseOrder + ", remarks=" + remarks + ", addParam1=" + addParam1
				+ ", addParam2=" + addParam2 + ", addParam3=" + addParam3 + ", addParam4=" + addParam4
				+ ", parentCustId=" + parentCustId + ", invoiceOption=" + invoiceOption + ", oldPassword1="
				+ oldPassword1 + ", oldPassword2=" + oldPassword2 + ", oldPassword3=" + oldPassword3
				+ ", firstActivationDate=" + firstActivationDate + ", allowedIpAddrs=" + allowedIpAddrs
				+ ", selfCarePwd=" + selfCarePwd + ", title=" + title + ", custName=" + custName + ", contactPerson="
				+ contactPerson + ", cafNo=" + cafNo + ", pan=" + pan + ", gst=" + gst + ", aadhar=" + aadhar
				+ ", macTelFlag=" + macTelFlag + ", mobile=" + mobile + ", altMobile=" + altMobile + ", altPhone="
				+ altPhone + ", altEmail=" + altEmail + ", fax=" + fax + ", resellerId=" + resellerId + ", salesRepId="
				+ salesRepId + ", deposit=" + deposit + ", voiceSrvType=" + voiceSrvType + ", didNo=" + didNo
				+ ", childDidNo=" + childDidNo + ", intercomNo=" + intercomNo + ", intercomGrp=" + intercomGrp
				+ ", onlineRenewalFlag=" + onlineRenewalFlag + ", voipEnableFlag=" + voipEnableFlag + ", custCategory="
				+ custCategory + ", walletBalance=" + walletBalance + ", networkType=" + networkType + ", oltSlotId="
				+ oltSlotId + ", oltPortId=" + oltPortId + ", strConnType=" + strConnType + ", strOltName=" + strOltName
				+ ", strSlotName=" + strSlotName + ", strPortName=" + strPortName + ", createdByStaffId="
				+ createdByStaffId + ", lastModifiedByStaffId=" + lastModifiedByStaffId + ", serviceAreaId="
				+ serviceAreaId + ", networkDeviceId=" + networkDeviceId + ", onuId=" + onuId + ", isDeleted="
				+ isDeleted + ", defaultPoolId=" + defaultPoolId + ", otp=" + otp + ", otpValidate=" + otpValidate
				+ ", createByName=" + createByName + ", updateByName=" + updateByName + ", lastModifiedDate="
				+ lastModifiedDate + ", latitude=" + latitude + ", longitude=" + longitude + ", url=" + url
				+ ", gisCode=" + gisCode + ", voiceProvision=" + voiceProvision + ", salesRemark=" + salesRemark
				+ ", serviceType=" + serviceType + ", previousCafApprover=" + previousCafApprover + ", nextCafApprover="
				+ nextCafApprover + ", cafApproveStatus=" + cafApproveStatus + ", mvnoId=" + mvnoId + ", calendarType="
				+ calendarType + ", invoiceType=" + invoiceType + ", nasPort=" + nasPort + ", framedIp=" + framedIp
				+ ", framedIpBind=" + framedIpBind + ", ipPoolNameBind=" + ipPoolNameBind + ", buid=" + buid
				+ ", maxConcurrentSession=" + maxConcurrentSession + ", gatewayIp=" + gatewayIp + ", skipNetConf="
				+ skipNetConf + ", rdImport=" + rdImport + ", mvnoDeactivationFlag=" + mvnoDeactivationFlag + ", ipv4="
				+ ipv4 + ", ipv6=" + ipv6 + ", vlan=" + vlan + ", nasPortId=" + nasPortId + ", nasIpAddress="
				+ nasIpAddress + ", framedIpv6Address=" + framedIpv6Address + ", delegatedPrefix=" + delegatedPrefix
				+ ", framedRoute=" + framedRoute + ", macProvision=" + macProvision + ", macAuthEnable=" + macAuthEnable
				+ ", framedIpNetmask=" + framedIpNetmask + ", framedIpv6Prefix=" + framedIpv6Prefix + ", primaryDns="
				+ primaryDns + ", primaryIpv6Dns=" + primaryIpv6Dns + ", secondaryIpv6Dns=" + secondaryIpv6Dns
				+ ", secondaryDns=" + secondaryDns + ", macRetentionPeriod=" + macRetentionPeriod
				+ ", macRetentionUnit=" + macRetentionUnit + ", nextQuotaResetDate=" + nextQuotaResetDate + ", blockNo="
				+ blockNo + ", quotas=" + quotas + "]";
	}

	public Customer() {
	}

}

