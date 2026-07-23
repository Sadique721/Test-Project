package com.diameter.kafka;

import com.diameter.dto.CustPlanMapppingPojo;
import com.diameter.model.CustSmsDetails;
import com.diameter.model.CustVoiceDetails;
import com.diameter.model.CustomerServiceMapping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveCustomerDataShareMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String accountNumber;
    private String createdByName;
    private Boolean istrialplan = false;
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
    private String serviceAreaName;
    private String partnerName;
    private String planPurchaseType;
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
    private Integer createdById;
    private Integer lastModifiedById;
    private String lastModifiedByName;
    private String serialNumber;
    private Integer serviceId;
    private String ipv4;
    private String ipv6;
    private String vlan;
    private String blockNo;
    private Integer billDay;
    private Integer refMvno;
    private Boolean isCaptiveportal;
    private String referenceNo;
    private Integer earlybilldays;
    private Integer earlybillday;
    private LocalDate earlybilldate;
    private String pan;
    private String drivingLicence;
    private String customerVrn;
    private String passportNo;
    private String customerNid;
    private Integer renewPlanLimit;
    private Integer graceDay;
    private Integer departmentId;
    private String currency;
    private boolean billDayUpdated;
    private Integer previousBillday;
    private String contactperson;
    private  String nextbilldate;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    private List<CustSmsDetails> custSmsDetailsList = new ArrayList<>();
    private List<CustVoiceDetails> custVoiceDetailsList = new ArrayList<>();
    private CustSmsDetails custSmsDetails;
    private CustVoiceDetails custVoiceDetails;


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
    private LocalDateTime lastStatusChangeDate;
    private LocalDate expiryDate;
    private LocalDateTime createDate;
    private Integer failCount;
    private LocalDateTime updateDate;

    private String altMobile;
    private Integer maxConcurrentSession;
    private Boolean macProvision;
    private Boolean macAuthEnable;
    private LocalDate nextQuotaResetDate;
    private Integer macRetentionPeriod;
    private String macRetentionUnit;
    private Boolean isinvoicestop;

}
