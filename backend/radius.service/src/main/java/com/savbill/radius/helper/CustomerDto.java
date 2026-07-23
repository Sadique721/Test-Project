package com.savbill.radius.helper;

import com.savbill.radius.entity.CustomerQosPolicyMapping;
import com.savbill.radius.entity.CustomerReply;
import com.savbill.radius.entity.CustomerTimeBasePolicyMapping;
import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.kafka.MessageConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "Customer", description = "This is data transfer object for customer which is used to create new customer")
public class CustomerDto {
    @ApiModelProperty(notes = "Name of the user", required = true)
    private String userName;

    @ApiModelProperty(notes = "Password of the user", required = true)
    private String password;

    @ApiModelProperty(notes = "This is Email Address", required = true)
    private String emailAddress;

    @ApiModelProperty(notes = "This is Country Code", required = false)
    private String countryCode;

    @ApiModelProperty(notes = "This is Mobile No", required = true)
    private String mobileNo;

    @ApiModelProperty(notes = "Status of the customer", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
    private String customerStatus;

    @ApiModelProperty(notes = "This is fail count", required = false)
    private Long failCount;

    @ApiModelProperty(notes = "Mac address of the user", required = false)
    private String macAddress;

    @ApiModelProperty(notes = "This is customer Qos policy name", required = false)
    private String qosPolicyName;

    @ApiModelProperty(notes = "This is customer concurrent policy", required = false)
    private Integer concurrentPolicyCount;

    @OneToMany(targetEntity = MacAddressMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private Set<MacAddressMapping> macAddressMapping;

    @OneToMany(targetEntity = CustomerReply.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private Set<CustomerReply> customerReplyList;

    @ApiModelProperty(hidden = true)
    private Integer mvnoId;

    @ApiModelProperty(notes = "This is plan id")
    private Long planId;

    @ApiModelProperty(notes = "This is voucher id")
    private Long voucherId;

    @ApiModelProperty(notes = "This is plan type")
    private String planType;

    @ApiModelProperty(notes = "This is plan name")
    private String planName;

    @ApiModelProperty(notes = "This is voucher code")
    private String voucherCode;

    @ApiModelProperty(notes = "Time based total available quota")
    private String timeBasedTotalQuota;

    @ApiModelProperty(notes = "Time based used quota")
    private String timeBasedUsedQuota;

    @ApiModelProperty(notes = "Time based unused quota")
    private String timeBasedUnusedQuota;

    @ApiModelProperty(notes = "Volume based total available quota")
    private String volumeBasedTotalQuota;

    @ApiModelProperty(notes = "Volume based used quota")
    private String volumeBasedUsedQuota;

    @ApiModelProperty(notes = "Volume based unused quota")
    private String volumeBasedUnusedQuota;

    @ApiModelProperty(notes = "This is plan Upload speed")
    private String uploadSpeed;

    @ApiModelProperty(notes = "This is plan Download  speed")
    private String downloadSpeed;

    @ApiModelProperty(notes = "This is Unlimited plan")
    private Boolean unlimitedPlan;

    @ApiModelProperty(notes = "This is Base upload qos")
    private Long baseUploadQos;


    @ApiModelProperty(notes = "This is Base download qos")
    private Long baseDownloadQos;
    
    @ApiModelProperty(notes = "This is Allow Cross Recharge flag")
    private Boolean allowCrossRecharge;

    @JsonIgnore
    private String sourceName;

    @ApiModelProperty(notes = "This is slice chunk details")
    private Long sliceChunk;

    @ApiModelProperty(notes = "This is time base policy mapping details")
    @OneToMany(targetEntity = CustomerTimeBasePolicyMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private List<CustomerTimeBasePolicyMapping> customerTimeBasePolicyMappings;
    
    @ApiModelProperty(notes = "This is Customer Qos policy mapping details")
    @OneToMany(targetEntity = CustomerQosPolicyMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private List<CustomerQosPolicyMapping> customerQosPolicyMappings;

//    @ApiModelProperty(notes = "This is location Id")
//    private Long locationId;
    
    @ApiModelProperty(notes = "This is location names",hidden=true)
    private List<Long> locations;
    
    @ApiModelProperty(notes = "This is Customer start Date")
    private String startDate;
    
    @ApiModelProperty(notes = "This is Customer end Date")
    private String endDate;

    @ApiModelProperty(notes = "Quota Reset Interval")
    private String quotaResetInterval;

    @ApiModelProperty(notes = "Override maximum concurrent session")
    private Integer maxconcurrentsession;
    
    public CustomerDto(CustomMessage customMessage) {
        Map<String, Object> message = customMessage.getCustomerData();
        if (message.get("concurrentPolicyCount") != null) {
            this.concurrentPolicyCount = Integer.parseInt(message.get("concurrentPolicyCount").toString());
        }
        if (message.get("userName") != null) {
            this.userName = message.get("userName").toString();
        }
        if (message.get("password") != null) {
            this.password = message.get("password").toString();
        }
        if (message.get("emailId") != null) {
            this.emailAddress = message.get("emailId").toString();
        }
        if (message.get("mobileNo") != null) {
            this.mobileNo = message.get("mobileNo").toString();
        }
        if (message.get("macAddress") != null) {
            this.macAddress = message.get("macAddress").toString();
        }
        if (message.get("failCount") != null) {
            this.failCount = Long.parseLong(message.get("failCount").toString());
        }
        if (message.get("qosPolicyName") != null) {
            this.qosPolicyName = message.get("qosPolicyName").toString();
        }
        if (message.get("customerStatus") != null) {
            this.setCustomerStatus(message.get("customerStatus").toString());
        }

        if (customMessage.getSourceName() != null) {
            this.sourceName = customMessage.getSourceName();
        } else {
            this.sourceName = MessageConstants.SOURCE_NAME_SAVBILL_RADIUS;
        }

        if (message.get("countryCode") != null) {
            this.countryCode = message.get("countryCode").toString();
        }

        if (message.get("mvnoId") != null) {
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
        }

        if (message.get("planId") != null) {
            this.planId = Long.parseLong(message.get("planId").toString());
        }

        if (message.get("voucherId") != null) {
            this.voucherId = Long.parseLong(message.get("voucherId").toString());
        }

        if (message.get("planType") != null) {
            this.planType = message.get("planType").toString();
        }

        if (message.get("planName") != null) {
            this.planName = message.get("planName").toString();
        }

        if (message.get("voucherCode") != null) {
            this.voucherCode = message.get("voucherCode").toString();
        }

        if (message.get("timeBasedTotalQuota") != null) {
            this.timeBasedTotalQuota = message.get("timeBasedTotalQuota").toString();
        }

        if (message.get("timeBasedUsedQuota") != null) {
            this.timeBasedUsedQuota = message.get("timeBasedUsedQuota").toString();
        }

        if (message.get("timeBasedUnusedQuota") != null) {
            this.timeBasedUnusedQuota = message.get("timeBasedUnusedQuota").toString();
        }

        if (message.get("volumeBasedTotalQuota") != null) {
            this.volumeBasedTotalQuota = message.get("volumeBasedTotalQuota").toString();
        }

        if (message.get("volumeBasedUsedQuota") != null) {
            this.volumeBasedUsedQuota = message.get("volumeBasedUsedQuota").toString();
        }

        if (message.get("volumeBasedUnusedQuota") != null) {
            this.volumeBasedUnusedQuota = message.get("volumeBasedUnusedQuota").toString();
        }

        if (message.get("uploadSpeed") != null) {
            this.uploadSpeed = message.get("uploadSpeed").toString();
        }

        if (message.get("downloadSpeed") != null) {
            this.downloadSpeed = message.get("downloadSpeed").toString();
        }

        if (message.get("baseUploadQos") != null) {
            this.baseUploadQos = Long.parseLong(message.get("baseUploadQos").toString());
        }

        if (message.get("baseDownloadQos") != null) {
            this.baseDownloadQos = Long.parseLong(message.get("baseDownloadQos").toString());
        }

        if (message.get("unlimitedPlan") != null) {
            this.unlimitedPlan = Boolean.parseBoolean(message.get("unlimitedPlan").toString());
        }

        if (customMessage.getMacAddressMapping() != null && !customMessage.getMacAddressMapping().isEmpty()) {
            this.macAddressMapping = new HashSet<>(customMessage.getMacAddressMapping());
        }

        if (message.get("sliceChunk") != null) {
            this.sliceChunk = Long.parseLong(message.get("sliceChunk").toString());

        if (message.get("allowCrossRecharge") != null) {
            this.allowCrossRecharge = Boolean.parseBoolean(message.get("allowCrossRecharge").toString());
        }
        if (message.get("slicechunk") != null) {
            this.sliceChunk = Long.parseLong(message.get("slicechunk").toString());

        }

        if (customMessage.getCustomerTimeBasePolicyMappings()!= null && !customMessage.getCustomerTimeBasePolicyMappings().isEmpty()) {
            this.customerTimeBasePolicyMappings = customMessage.getCustomerTimeBasePolicyMappings();
        }

        if (customMessage.getLocationIdList() != null && !customMessage.getLocationIdList().isEmpty()) 
        {
            this.locations=customMessage.getLocationIdList();
        }
            if(message.get("startDate") != null) {
                this.startDate = message.get("startDate").toString();
        }
            if(message.get("endDate") != null) {
                this.endDate = message.get("endDate").toString();
        }
        
        if (customMessage.getCustomerQosPolicyMapping()!= null && !customMessage.getCustomerQosPolicyMapping().isEmpty()) {
            this.customerQosPolicyMappings = customMessage.getCustomerQosPolicyMapping();
        }
       
        if(message.get("quotaResetInterval") != null)
        {
            this.quotaResetInterval = message.get("quotaResetInterval").toString();
        }

        if(message.get("maxconcurrentsession") != null)
        {
            this.maxconcurrentsession = Integer.valueOf(message.get("maxconcurrentsession").toString());
        }
    }
}}
