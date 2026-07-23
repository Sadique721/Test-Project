package com.diameter.kafka;

import com.diameter.dto.CustPlanMapppingPojo;
import com.diameter.model.CustomerServiceMapping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateCustomerShareDataMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String email;
    private String mobile;
    private String countryCode;
    private Integer serviceAreaId;
    private Integer networkdevicesId;
    private String status;
    private String custtype;
    private String phone;
    private Integer mvnoId;
    private Long buId;
    private Integer lcoId;
    private Boolean is_from_pwc;
    private Boolean isDeleted;
    private Long oltslotid;
    private Long oltportid;
    private String fullName;
    private Integer parnterId;
    private String planPurchaseType;
    private String serviceAreaName;
    private String partnerName;
    private String calendarType;
    private String dunningCategory;
    private String parentCustUsername;
    private Integer parentCustId;
    private String feasibilityRequired;
    private Long popId;
    private Long oltId;
    private Long masterdbid;
    private Long splitterid;
    private String framedIp;
    private String framedIpBind;
    private String ipPoolNameBind;
    private String nasPort;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private String contactperson;
    private Integer createdById;
    private Integer lastModifiedById;
    private String serialNumber;
    private Integer serviceId;

//    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private  String nextbilldate;

    private Boolean istrialplan;
    private String framedIpv6Address;


    private String delegatedprefix;
    private String nasPortId;
    private Boolean mac_provision;
    private Boolean mac_auth_enable;
    private Integer  macRetentionPeriod;
    private String  macRetentionUnit;
    private String  secondaryDNS;
    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String  primaryDNS;
    private String  primaryIPv6DNS;
    private String  secondaryIPv6DNS;
    private String blockNo;
    private String vlanId;
    private String gatewayIP;
    private String framedroute;
    private String accountNo;
    private String pan;

    private String customerVrn;

    private String customerNid;

    private Integer renewPlanLimit;

    private String passportNo;

    private String drivingLicence;
    private Integer billday;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String quotaResetDate;
    private boolean billDayUpdated;
    private Integer previousBillday;

    //TODO Arpit : Added missing fields
    private String serviceType;
    private Boolean voiceProvision;
    private Double walletBalance;
    private Boolean voipEnableFlag;
    private Boolean onlineRenewalFlag;
    private String didNo;
    private String voiceSrvType;
    private String addParam1;
    private String addParam2;
    private String addParam3;
    private String addParam4;
    private Boolean mactelFlag;
    private String gst;
    private String aadhar;
    private String selfCarePwd;
//    private LocalDateTime lastStatusChangeDate;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String lastStatusChangeDate;
//    private LocalDate expiryDate;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String expiryDate;
//    private LocalDateTime createDate;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String createDate;
    private Integer failCount;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String updateDate;
//    private LocalDateTime updateDate;

    private String altMobile;
//    @JsonProperty("max_concurrent_session")
    private Integer maxConcurrentSession;
//    private Integer maxConcurrentsession;
    private Boolean macProvision;
    private Boolean macAuthEnable;
    @JsonDeserialize(using = FlexibleDateStringDeserializer.class)
    private String nextQuotaResetDate;
    private Boolean isinvoicestop;
//    private LocalDate nextQuotaResetDate;





}
