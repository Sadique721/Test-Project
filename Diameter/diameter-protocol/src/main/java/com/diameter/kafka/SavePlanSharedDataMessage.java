package com.diameter.kafka;

import com.diameter.dto.Productplanmappingdto;
import com.diameter.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavePlanSharedDataMessage {
    private Integer id;
    private String name;
    private String displayName;
    private String code;
    private String desc;
    private String category;
    private Integer maxChild;
    private String startDate;
    private String endDate;
    private Long quota;
    private String quotaUnit;
    private String uploadQOS;
    private String downloadQOS;
    private String uploadTs;
    private String downloadTs;
    private Boolean allowOverUsage;
    private String status;
    private String planStatus;
    private Long childQuota;
    private String childQuotaUnit;
    private Long slice;
    private String sliceUnit;
    private String attachedToAllHotSpots;
    private String param1;
    private String param2;
    private Integer mvnoId;
    private Integer taxId;
    private Integer serviceId;
    private Integer timebasepolicyId;
    private String plantype;
    private Double dbr;
    private List<PostpaidPlanCharge> chargeList;
    private String planGroup;
    private Double validity;
    private String saccode;
    private String maxconcurrentsession;
    private String quotaunittime;
    private Double quotatime;
    private String quotatype;
    private Double offerprice;
    private Double quotadid;
    private Double quotaintercom;
    private String quotaunitdid;
    private String quotaunitintercom;
    private QOSPolicy qospolicy;
    private List<RadiusProfile> radiusprofile;
    private Boolean isDelete;
    private String dataCategory;
    private Double taxamount;
    private List<ServiceArea> serviceAreaNameList;
    private String quotaResetInterval;
    private String mode;
    private String unitsOfValidity;
    private Long buId;
    private Integer nextTeamHierarchyMapping;
    private Integer nextStaff;
    private Double newOfferPrice;
    @SerializedName("accessibility")
    private String Accessibility;
    private Long productId;
    private List<Productplanmappingdto> productplanmappingList;
    private Boolean invoiceToOrg;
    private Boolean requiredApproval;
    private List<PlanCasMapping> planCasMappingList;
    private String connection_type;
    private String location;
    private String quantity;
    private String package_type;
    private String number_of_days;
    private String no_of_users;
    private String ip_or_ip_pool;
    private String event_per_second;
    private String country;
    private String businessType;
    private Boolean basePlan;
    private Long templateId;
    private List<PlanQosMappingEntity> planQosMappingEntities;
    private Integer createdById;
    private Integer lastModifiedById;
    private Boolean isApprove = false;
    private List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappingList;
    private Long qospolicy_id;
    private String qospolicy_name;
    private String usageQuotaType;

    private Long smsLimit;
    private Long voiceLimit;

    private String smsResetInterval;
    private String voiceResetInterval;
    private String smstype;
    private String voicetype;
    private String pulse;

    private String param3;

    private Boolean useQuota;
    private Double chunk;
    private Boolean addonToBase;
    private Boolean allowdiscount;

    private String currency;

    private Integer maxHoldDurationDays;
    private Integer maxHoldAttempts;
    private String mvnoName;
    private Long smsRatePackageGroup;
    private Long dataRatePackageGroup;
    private Long voiceRatePackageGroup;
}