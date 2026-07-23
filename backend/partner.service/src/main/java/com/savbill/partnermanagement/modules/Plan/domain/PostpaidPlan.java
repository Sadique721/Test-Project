package com.savbill.partnermanagement.modules.Plan.domain;




import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.data.IBaseData;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "TBLMPOSTPAIDPLAN")
@EntityListeners(AuditableListener.class)
public class PostpaidPlan extends Auditable implements IBaseData {

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

    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "TAXID", length = 40)
    private Integer taxId;

    @Column(name = "serviceid", length = 40)
    private Integer serviceId;

    @Column(name = "timebasepolicyid", length = 40)
    private Integer timebasepolicyId;

    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

    @Column(name = "dbr", nullable = false, length = 40)
    private Double dbr;

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

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "datacategory")
    private String dataCategory;

    private Double taxamount;

    @Transient
    private String serviceName;

    @Transient
    private String timebasepolicyName;

    @Column(name = "quotarestinterval", nullable = false, length = 40)
    private String quotaResetInterval;

    @Column(nullable = false, length = 40, columnDefinition = "varchar(255) default 'NORMAL'")
    private String mode;

    @Column(name = "unitsofvalidity", nullable = false, length = 40, columnDefinition = "varchar(100) default 'Days'")
    private String unitsOfValidity;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMapping;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "new_offer_price")
    private Double newOfferPrice;

    @Column(name = "Accessibility")
    private String Accessibility;

    @Column(name = "allowdiscount")
    private boolean allowdiscount;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "invoicetoorg")
    private Boolean invoiceToOrg;

    @Column(name = "requiredapproval")
    private Boolean requiredApproval;

    @Column(name = "bandwidth")
    private String bandwidth;
    @Column(name = "linktype")
    private String link_type;
    @Column(name = "connectiontype")
    private String connection_type;
    @Column(name = "distance")
    private String distance;
    @Column(name = "ram")
    private String ram;
    @Column(name = "cpu")
    private String cpu;
    @Column(name = "storage")
    private String storage;
    @Column(name = "storagetype")
    private String storage_type;
    @Column(name = "autobackup")
    private String auto_backup;
    @Column(name = "cpanel")
    private String cpanel;
    @Column(name = "location")
    private String location;
    @Column(name = "quantity")
    private String quantity;
    @Column(name = "packagetype")
    private String package_type;
    @Column(name = "numberofdays")
    private String number_of_days;
    @Column(name = "numberoofusers")
    private String no_of_users;
    @Column(name = "rackspace")
    private String rack_space;
    @Column(name = "rackunit")
    private String rack_unit;
    @Column(name = "powerconsumption")
    private String power_consumption;
    @Column(name = "networkcard")
    private String network_card;
    @Column(name = "iporippool")
    private String ip_or_ip_pool;
    @Column(name = "numberoflicense")
    private String no_of_license;
    @Column(name = "noofemailuserlicense")
    private String no_of_email_user_license;
    @Column(name = "noofserverlicense")
    private String no_of_server_license;
    @Column(name = "noofuserlicense")
    private String no_of_user_license;
    @Column(name = "noofnodes")
    private String no_of_nodes;
    @Column(name = "eventpersecond")
    private String event_per_second;
    @Column(name = "noofadditionalserver")
    private String no_of_additional_server;
    @Column(name = "noofadditionalstorage")
    private String no_of_additional_storage;
    @Column(name = "additionalstoragetype")
    private String additional_storage_type;
    @Column(name = "epslicense")
    private String eps_License;
    @Column(name = "noofnodeslicense")
    private String no_of_nodes_license;
    @Column(name = "hardwareresource")
    private String hardware_resource;
    @Column(name = "manpower")
    private String man_power;
    @Column(name = "noofdomains")
    private String no_of_domains;
    @Column(name = "securitymodules")
    private String security_modules;
    @Column(name = "hardwareorservers")
    private String hardware_or_servers;
    @Column(name = "country")
    private String country;
    @Column(name = "noofvpn")
    private String no_of_vpn;
    @Column(name = "devicethroughput")
    private String device_throughput;
    @Column(name = "retail")
    private String retail;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "baseplan", columnDefinition = "Boolean default false")
    private Boolean basePlan = false;

    @Column(name = "template_id", length = 40)
    private Long templateId;
//    @ManyToOne
//    @JoinColumn(name = "qospolicy_id")
//    private QOSPolicy qospolicy;
    @Transient
    private Boolean isApprove = false;
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();
//    @ManyToMany
//    @JoinTable(name = "tblpostpaidplanradiusprofilerel", joinColumns = {@JoinColumn(name = "POSTPAIDPLANID")}, inverseJoinColumns = {@JoinColumn(name = "radiusprofileid")})
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<RadiusProfile> radiusprofile;
//
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JsonManagedReference
//    @OneToMany(targetEntity = PlanQosMappingEntity.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "planid")
//    private List<PlanQosMappingEntity> planQosMappingEntities;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblplanservicearearel", joinColumns = {@JoinColumn(name = "planid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    @ToString.Exclude
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    @Transient
    private List<Productplanmappingdto> productplanmappingList = new ArrayList<>();

    @Override
    public Serializable getPrimaryKey() {
        return null;
    }


    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
