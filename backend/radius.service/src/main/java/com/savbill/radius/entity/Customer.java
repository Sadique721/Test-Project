package com.savbill.radius.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.savbill.radius.helper.CustomerDto;
import com.savbill.radius.utils.RadiusUtils;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "TBLMCUSTOMERS")
@ApiModel(value = "Customer Entity", description = "This is Customer entity which is used to update customer data")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Customer Id")
	@Column(name = "custid", nullable = false)
	private Long customerId;

	@ApiModelProperty(notes = "Name of the user", required = true)
	@Column(name = "username", nullable = false, length = 250)
	private String userName;

	@ApiModelProperty(notes = "Password of the user", required = true)
	@Column(name = "password", nullable = false, length = 250)
	private String password;

	@ApiModelProperty(notes = "This is Email Address", required = true)
	@Column(name = "emailaddress", nullable = false, length = 100)
	private String emailAddress;

	@ApiModelProperty(notes = "This is country code", required = false)
	@Column(name = "countrycode", nullable = true, length = 10)
	private String countryCode;

	@ApiModelProperty(notes = "This is Mobile No", required = true)
	@Column(name = "mobileno", nullable = false, length = 25)
	private String mobileNo;

	@ApiModelProperty(notes = "Status of the customer", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
	@Column(name = "cstatus", nullable = false, length = 250)
	private String customerStatus;

	@ApiModelProperty(notes = "This is fail count", required = false)
	@Column(name = "failcount", nullable = true)
	private Long failCount;

	@ApiModelProperty(notes = "Mac address of the user", required = true)
	@Column(name = "MACADDRESS", nullable = true, length = 250)
	private String macAddress;

	@ApiModelProperty(notes = "This is customer Qos policy name")
	@Column(name = "qospolicyname", nullable = true, length = 250)
	private String qosPolicyName;
	
	@ApiModelProperty(notes = "This is customer Qos policy name")
	@Column(name = "allowcrossrecharge", nullable = true)
	private Boolean allowCrossRecharge;

	@ApiModelProperty(hidden = true)
	@Column(name = "concurrentpolicycount", nullable = false)
	private Integer concurrentPolicyCount;

	@ApiModelProperty(hidden = true)
	@Column(name = "lastlogintime")

	private Timestamp lastLoginTime;
	@ApiModelProperty(hidden = true)

	@Column(name = "lastpasswordchange")
	private Timestamp lastPasswordChange;

	@ApiModelProperty(hidden = true)
	@Column(name = "createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;

	@ApiModelProperty(hidden = true)
	@Column(name = "lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

	@Column(name = "planid", nullable = true)
	private Long planId;

	@Column(name = "voucherid", nullable = true)
	private Long voucherId;

	@ApiModelProperty(notes = "This is plan type")
	@Column(name = "plantype", nullable = false, length = 25)
	private String planType;

	@ApiModelProperty(notes = "This is plan name")
	@Column(name = "planname", nullable = false, length = 250)
	private String planName;

	@ApiModelProperty(notes = "This is voucher code")
	@Column(name = "vouchercode", nullable = true, length = 250)
	private String voucherCode;

	@ApiModelProperty(notes = "Time based total available quota")
	@Column(name = "timebasedtotalquota", nullable = true, length = 50)
	private String timeBasedTotalQuota;

	@ApiModelProperty(notes = "Time based used quota")
	@Column(name = "timebasedusedquota", nullable = true, length = 50)
	private String timeBasedUsedQuota;

	@ApiModelProperty(notes = "Time based unused quota")
	@Column(name = "timebasedunusedquota", nullable = true, length = 50)
	private String timeBasedUnusedQuota;

	@ApiModelProperty(notes = "Volume based total available quota")
	@Column(name = "volumebasedtotalquota", nullable = true, length = 50)
	private String volumeBasedTotalQuota;

	@ApiModelProperty(notes = "Volume based used quota")
	@Column(name = "volumebasedusedquota", nullable = true, length = 50)
	private String volumeBasedUsedQuota;

	@ApiModelProperty(notes = "Volume based unused quota")
	@Column(name = "volumebasedunusedquota", nullable = true, length = 50)
	private String volumeBasedUnusedQuota;

	@ApiModelProperty(notes = "This is plan Upload speed")
	@Column(name = "uploadspeed", nullable = false, length = 20)
	private String uploadSpeed;

	@ApiModelProperty(notes = "This is plan Download  speed")
	@Column(name = "downloadspeed", nullable = false, length = 20)
	private String downloadSpeed;

	@ApiModelProperty(notes = "This is Unlimited plan")
	@Column(name = "unlimitedplan", nullable = false, length = 20)
	private Boolean unlimitedPlan;

	@ApiModelProperty(notes = "This is Base upload qos")
	@Column(name = "baseuploadqos", nullable = true, length = 20)
	private Long baseUploadQos;

	@ApiModelProperty(notes = "This is Base download Qos")
	@Column(name = "basedownloadqos", nullable = true, length = 20)
	private Long baseDownloadQos;

	@ApiModelProperty(hidden = true)
	@Column(name = "mvnoid", nullable = false)
	private Integer mvnoId;

	@Transient
	@OneToMany(targetEntity = MacAddressMapping.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "custid", referencedColumnName = "custid")
	private Set<MacAddressMapping> macAddressMapping;

	@OneToMany(targetEntity = CustomerReply.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "custid", referencedColumnName = "custid")
	private Set<CustomerReply> customerReplyList;

	@ApiModelProperty(notes = "This is slicechunk support")
	@Column(name = "slicechunk", nullable = false)
	private Long sliceChunk;


	@OneToMany(targetEntity = CustomerTimeBasePolicyMapping.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "custid", referencedColumnName = "custid")
	private List<CustomerTimeBasePolicyMapping> customerTimeBasePolicyMappings;
	
	@OneToMany(targetEntity = CustomerQosPolicyMapping.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "custid", referencedColumnName = "custid")
	private List<CustomerQosPolicyMapping> customerQosPolicyMappings;

	/*@ApiModelProperty(notes = "This is locationId")
	@Column(name = "location_id", nullable = false)
	private Long locationId;*/
	
	@ApiModelProperty(notes = "This is customer start date")
    @Column (name="startdate", nullable = false)
	private LocalDateTime startDate;
	
	@ApiModelProperty(notes = "This is customer end date")
    @Column (name="enddate", nullable = false)
	private LocalDateTime endDate;
	
	@ApiModelProperty(notes = "Quota Reset Interval")
    @Column (name="quotaresetinterval", nullable = false , length = 20)
    private String quotaResetInterval;

	@Column(name = "maxconcurrentsession")
	private Integer maxconcurrentsession;

	@Column(name = "mvno_deactivation_flag")
	private Boolean mvnoDeactivationFlag;

	public Customer(CustomerDto customerDto) {
		this.customerStatus = customerDto.getCustomerStatus();
		this.failCount = customerDto.getFailCount();
		this.mobileNo = customerDto.getMobileNo();
		this.userName = customerDto.getUserName();
		this.password = customerDto.getPassword();
		this.customerReplyList = customerDto.getCustomerReplyList();
		this.sliceChunk = customerDto.getSliceChunk();
		this.allowCrossRecharge = customerDto.getAllowCrossRecharge();
		// this.macAddressMapping = customerDto.getMacAddressMapping();
		this.concurrentPolicyCount=customerDto.getConcurrentPolicyCount();
		this.planId = customerDto.getPlanId();
		this.voucherId = customerDto.getVoucherId();
		this.planName = customerDto.getPlanName();
		this.planType = customerDto.getPlanType();
		this.voucherCode = customerDto.getVoucherCode();
		this.timeBasedTotalQuota = customerDto.getTimeBasedTotalQuota();
		this.timeBasedUnusedQuota = customerDto.getTimeBasedUnusedQuota();
		this.timeBasedUsedQuota = customerDto.getTimeBasedUsedQuota();
		this.volumeBasedTotalQuota = customerDto.getVolumeBasedTotalQuota();
		this.volumeBasedUnusedQuota = customerDto.getVolumeBasedUnusedQuota();
		this.volumeBasedUsedQuota = customerDto.getVolumeBasedUsedQuota();
		this.uploadSpeed = customerDto.getUploadSpeed();
		this.downloadSpeed = customerDto.getDownloadSpeed();
		this.unlimitedPlan = customerDto.getUnlimitedPlan();
		if(customerDto.getStartDate() != null) {
			if(RadiusUtils.isValidFormat("yyyy-MM-dd HH:mm:ss", customerDto.getStartDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				this.startDate = LocalDateTime.parse(customerDto.getStartDate(), formatter);
			}
			if(RadiusUtils.isValidFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", customerDto.getStartDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
				this.startDate = LocalDateTime.parse(customerDto.getStartDate(), formatter);
			}
		}
		if(customerDto.getEndDate() != null) {
			if(RadiusUtils.isValidFormat("yyyy-MM-dd HH:mm:ss", customerDto.getEndDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				this.endDate = LocalDateTime.parse(customerDto.getEndDate(), formatter);
			}
			if(RadiusUtils.isValidFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS", customerDto.getEndDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
				this.endDate = LocalDateTime.parse(customerDto.getEndDate(), formatter);
			}
		}

		this.baseDownloadQos = customerDto.getBaseDownloadQos();
		this.baseUploadQos = customerDto.getBaseUploadQos();

		if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getCountryCode())) {
			this.countryCode = customerDto.getCountryCode();
		} else {
			this.countryCode = null;
		}
//		if(ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getMacAddress()))
//		{
//			this.macAddress = customerDto.getMacAddress();
//		}
//		else
//		{
//			this.macAddress = null;
//		}
		if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getQosPolicyName())) {
			this.qosPolicyName = customerDto.getQosPolicyName();
		} else {
			this.qosPolicyName = null;
		}
		if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getEmailAddress())) {
			this.emailAddress = customerDto.getEmailAddress();
		} else {
			this.emailAddress = null;
		}
		this.quotaResetInterval = customerDto.getQuotaResetInterval();
		if(customerDto.getMaxconcurrentsession() != null)
			this.maxconcurrentsession = customerDto.getMaxconcurrentsession();
	}

	public Customer(UpdateCustomerDto updateCustomerDto, Customer customer) 
	{
		this.customerId = customer.getCustomerId();
		this.customerStatus = updateCustomerDto.getCustomerStatus();
		this.failCount = updateCustomerDto.getFailCount();
		this.emailAddress = updateCustomerDto.getEmailAddress();
		this.mobileNo = updateCustomerDto.getMobileNo();
		this.userName = updateCustomerDto.getUserName();
		this.sliceChunk = updateCustomerDto.getSliceChunk();
		this.concurrentPolicyCount = updateCustomerDto.getConcurrentPolicyCount();
		this.allowCrossRecharge = updateCustomerDto.getAllowCrossRecharge();
		this.planId = customer.getPlanId();
		this.voucherId = customer.getVoucherId();
		this.planName = customer.getPlanName();
		this.planType = customer.getPlanType();
		this.voucherCode = customer.getVoucherCode();
		this.timeBasedTotalQuota = customer.getTimeBasedTotalQuota();
		this.timeBasedUnusedQuota = customer.getTimeBasedUnusedQuota();
		this.timeBasedUsedQuota = customer.getTimeBasedUsedQuota();
		this.volumeBasedTotalQuota = customer.getVolumeBasedTotalQuota();
		this.volumeBasedUnusedQuota = customer.getVolumeBasedUnusedQuota();
		this.volumeBasedUsedQuota = customer.getVolumeBasedUsedQuota();
		this.uploadSpeed = customer.getUploadSpeed();
		this.downloadSpeed = customer.getDownloadSpeed();
		this.unlimitedPlan = customer.getUnlimitedPlan();
		this.quotaResetInterval = customer.getQuotaResetInterval();
		this.baseDownloadQos = updateCustomerDto.getBaseDownloadQos();
		this.baseUploadQos = updateCustomerDto.getBaseUploadQos();

		this.customerReplyList = updateCustomerDto.getCustomerReplyList();
//		this.locationId = updateCustomerDto.getLocationId();
		if (ValidateCrudTransactionData.validateStringTypeFieldValue(updateCustomerDto.getQosPolicyName())) {
			this.qosPolicyName = updateCustomerDto.getQosPolicyName();
		} else {
			this.qosPolicyName = null;
		}
		if (ValidateCrudTransactionData.validateStringTypeFieldValue(updateCustomerDto.getEmailAddress())) {
			this.emailAddress = updateCustomerDto.getEmailAddress();
		} else {
			this.emailAddress = null;
		}
		if (ValidateCrudTransactionData.validateStringTypeFieldValue(updateCustomerDto.getCountryCode())) {
			this.countryCode = updateCustomerDto.getCountryCode();
		} else {
			this.countryCode = null;
		}
		
//		if(updateCustomerDto.getStartDate() != null) {
//			this.startDate = LocalDateTime.parse(updateCustomerDto.getStartDate(), DateTimeFormatter.ISO_DATE_TIME);
//		}
//		if(updateCustomerDto.getEndDate() != null) {
//			this.endDate = LocalDateTime.parse(updateCustomerDto.getEndDate(), DateTimeFormatter.ISO_DATE_TIME);
//		}
		
		this.startDate = customer.getStartDate();
		this.endDate = customer.getEndDate();
		if(updateCustomerDto.getMaxconcurrentsession() != null)
			this.maxconcurrentsession = updateCustomerDto.getMaxconcurrentsession();
	}
}
