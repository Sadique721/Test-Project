package com.savbill.integrationsystem.PostpaidPlan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Data
@ToString
@Table(name = "TBLMPOSTPAIDPLAN")
public class PostpaidPlan {

    @Id
    @Column(name = "POSTPAIDPLANID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DISPLAYNAME", nullable = false, length = 40)
    private String displayName;

    @Column(name = "PLANCODE", nullable = false, length = 40)
    private String code;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String desc;

    @Column(name = "PLANCATEGORY", nullable = false, length = 40)
    private String category;

    @Column(name = "MAXALLOWEDCHILD", length = 40)
    private Integer maxChild;

    @Column(name = "STARTDATE", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "ENDDATE", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "QUOTA", length = 40)
    private Long quota;

    @Column(name = "QUOTAUNIT", length = 40)
    private String quotaUnit;

    @Column(name = "UPLOADQOS", length = 40)
    private String uploadQOS;

    @Column(name = "DOWNLOADQOS", length = 40)
    private String downloadQOS;

    @Column(name = "UPLOADTS", length = 40)
    private String uploadTs;

    @Column(name = "DOWNLOADTS", length = 40)
    private String downloadTs;

    @Column(name = "allowoverusage", length = 3)
    private Boolean allowOverUsage;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "PLANSTATUS", length = 40)
    private String planStatus;

    @Column(name = "CHILDQUOTA", length = 40)
    private Long childQuota;

    @Column(name = "CHILDQUOTAUNIT", length = 40)
    private String childQuotaUnit;

    @Column(name = "SLICE", length = 40)
    private Long slice;

    @Column(name = "SLICEUNIT", length = 40)
    private String sliceUnit;

    @Column(name = "ATTACHEDTOALLHOTSPOT", length = 40)
    private String attachedToAllHotSpots;

    @Column(name = "PARAM1", length = 40)
    private String param1;

    @Column(name = "PARAM2", length = 40)
    private String param2;

    @Column(name = "PARAM3", length = 40)
    private String param3;

    @Column(name = "MVNOID", length = 40)
    private Integer mvnoId;

    @Column(name = "TAXID", length = 40)
    private Integer taxId;

    @Column(name = "serviceid", length = 40)
    private Integer serviceId;

    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

    @Column(name = "dbr", nullable = false, length = 40)
    private Double dbr;
/*
    @JsonManagedReference
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();
*/

    @Column(name = "plangroup", nullable = false, length = 100)
    private String planGroup;

    @Column(name = "validity", length = 4)
    private Double validity;

    @Column(nullable = true)
    private String saccode;

    @Column(nullable = false)
    private String maxconcurrentsession;

    private String quotaunittime;

    private Double quotatime;

    @Column(nullable = false)
    private String quotatype;

    private Double offerprice;

    private Double quotadid;
    private Double quotaintercom;
    private String quotaunitdid;
    private String quotaunitintercom;

    @Column(name = "qospolicy_id")
    private Long qosPolicyId;

    @Column(name = "timebasepolicyid")
    private Long timebasepolicyId;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "datacategory")
    private String dataCategory;

    private Double taxamount;

    @Transient
    private String serviceName;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @Column(name = "quotarestinterval", nullable = false, length = 40)
    private String  quotaResetInterval;

    @Column(name = "unitsofvalidity",nullable = false, length = 40, columnDefinition = "varchar(100) default 'Days'")
    private String unitsOfValidity;

    @Column(name = "plan_id")
    private Integer planId;

//    @Transient
//    private List<PlanQosMappingEntity> planQosMappingEntities = new ArrayList<>();

    public PostpaidPlan(){}

    public PostpaidPlan(Map message){
//        Map<String, Object> message = (Map<String, Object>) customMessage.getData();
        if (message.get("id") != null)
            this.id = Integer.parseInt(message.get("id").toString());
        if (message.get("name") != null)
            this.name = message.get("name").toString();
        if (message.get("displayName") != null)
            this.displayName = message.get("displayName").toString();
        if (message.get("code") != null)
            this.code = message.get("code").toString();
        if (message.get("desc") != null)
            this.desc = message.get("desc").toString();
        if (message.get("category") != null)
            this.category = message.get("category").toString();
        if (message.get("startDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.startDate = LocalDate.parse(message.get("startDate").toString(), formatter);
        }
        if (message.get("endDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.endDate = LocalDate.parse(message.get("endDate").toString(), formatter);
        }
        if (message.get("uploadQOS") != null)
            this.uploadQOS = message.get("uploadQOS").toString();
        if (message.get("downloadQOS") != null)
            this.downloadQOS = message.get("downloadQOS").toString();
        if (message.get("uploadTs") != null)
            this.uploadTs = message.get("uploadTs").toString();
        if (message.get("downloadTs") != null)
            this.downloadTs = message.get("downloadTs").toString();
        if (message.get("allowOverUsage") != null)
            this.allowOverUsage = Boolean.parseBoolean(message.get("allowOverUsage").toString());
        if (message.get("quotaUnit") != null)
            this.quotaUnit = message.get("quotaUnit").toString();
        if (message.get("quota") != null)
            this.quota = Long.parseLong(message.get("quota").toString());
        if (message.get("planStatus") != null)
            this.planStatus = message.get("planStatus").toString();
        if (message.get("childQuota") != null)
            this.childQuota = Long.parseLong(message.get("childQuota").toString());
        if (message.get("childQuotaUnit") != null)
            this.childQuotaUnit = message.get("childQuotaUnit").toString();
        if (message.get("slice") != null)
            this.slice = Long.parseLong(message.get("slice").toString());
        if (message.get("sliceUnit") != null)
            this.sliceUnit = message.get("sliceUnit").toString();
        if (message.get("attachedToAllHotSpots") != null)
            this.attachedToAllHotSpots = message.get("attachedToAllHotSpots").toString();
        if (message.get("param1") != null)
            this.param1 = message.get("param1").toString();
        if (message.get("param2") != null)
            this.param2 = message.get("param2").toString();
        if (message.get("param3") != null)
            this.param3 = message.get("param3").toString();
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
        if (message.get("status") != null)
            this.status = message.get("status").toString();
        if (message.get("taxId") != null)
            this.taxId = Integer.parseInt(message.get("taxId").toString());
        if (message.get("serviceId") != null)
            this.serviceId = Integer.parseInt(message.get("serviceId").toString());
        if (message.get("serviceName") != null)
            this.serviceName = message.get("serviceName").toString();
        if (message.get("plantype") != null)
            this.plantype = message.get("plantype").toString();
        if (message.get("maxChild") != null)
            this.maxChild = Integer.parseInt(message.get("maxChild").toString());
//        if (message.get("chargeList") != null)
//            this.chargeList = message.get("chargeList").toString();
        if (message.get("dbr") != null)
            this.dbr = Double.parseDouble(message.get("dbr").toString());
        if (message.get("planGroup") != null)
            this.planGroup = message.get("planGroup").toString();
        if (message.get("validity") != null)
            this.validity = Double.parseDouble(message.get("validity").toString());
        if (message.get("saccode") != null)
            this.saccode = message.get("saccode").toString();
        if (message.get("maxconcurrentsession") != null)
            this.maxconcurrentsession = message.get("maxconcurrentsession").toString();
        if (message.get("quotaunittime") != null)
            this.quotaunittime = message.get("quotaunittime").toString();
        if (message.get("quotatime") != null)
            this.quotatime = Double.parseDouble(message.get("quotatime").toString());
        if (message.get("quotatype") != null)
            this.quotatype = message.get("quotatype").toString();
        if (message.get("offerprice") != null)
            this.offerprice = Double.parseDouble(message.get("offerprice").toString());
        if (message.get("qospolicyid") != null)
            this.qosPolicyId = Long.parseLong(message.get("qospolicyid").toString());
        if (message.get("timebasepolicyId") != null)
            this.timebasepolicyId = Long.parseLong(message.get("timebasepolicyId").toString());
//        if (message.get("radiusprofileIds") != null)
//            this.radiusProfileId = Long.parseLong(message.get("radiusprofileIds").toString());
        if (message.get("isDelete") != null)
            this.isDelete = Boolean.parseBoolean(message.get("isDelete").toString());
        if (message.get("quotadid") != null)
            this.quotadid = Double.parseDouble(message.get("quotadid").toString());
        if (message.get("quotaintercom") != null)
            this.quotaintercom = Double.parseDouble(message.get("quotaintercom").toString());
        if (message.get("quotaunitdid") != null)
            this.quotaunitdid = message.get("quotaunitdid").toString();
        if (message.get("quotaunitintercom") != null)
            this.quotaunitintercom = message.get("quotaunitintercom").toString();
        if (message.get("dataCategory") != null)
            this.dataCategory = message.get("dataCategory").toString();
        if (message.get("taxamount") != null)
            this.taxamount = Double.parseDouble(message.get("taxamount").toString());
        if (message.get("quotaresetInterval") != null)
            this.quotaResetInterval =message.get("quotaresetInterval").toString();
        if (message.get("unitsOfValidity") != null)
            this.unitsOfValidity =message.get("unitsOfValidity").toString();
//        if(message.get("planQosMappingList") != null){
//            this.planQosMappingEntities = message.get("planQosMappingList").toString();
//        }
//        if (message.get("serviceAreaIds") != null)
//            this.serviceAreaIds = Double.parseDouble(message.get("serviceAreaIds").toString());
//        if (message.get("serviceAreaNameList") != null)
//            this.serviceAreaNameList = Double.parseDouble(message.get("serviceAreaNameList").toString());
//        if (message.get("planQosMappingList") != null) {
//            List planQosMappingList = (List) message.get("planQosMappingList");
//            for (int i = 0; i < planQosMappingList.size(); i++) {
//                PlanQosMappingEntity planQosMappingEntity = new PlanQosMappingEntity((Map) planQosMappingList.get(i));
//                this.planQosMappingEntities.add(planQosMappingEntity);
//            }
//        }
    }
}
