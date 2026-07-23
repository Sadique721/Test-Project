package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.savbill.salescrmsbss.entity.pojo.PostpaidPlanPojo;
import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.pojo.Productplanmappingdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMPOSTPAIDPLAN")
public class PostpaidPlan{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTPAIDPLANID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DISPLAYNAME", nullable = false, length = 40)
    private String displayName;

    @Column(name = "PLANCATEGORY", nullable = false, length = 40)
    private String category;

    @Column(name = "STARTDATE", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "ENDDATE", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "QUOTA", length = 40)
    private Long quota;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();

    @Column(name = "plangroup", nullable = false, length = 100)
    private String planGroup;

    @Column(name = "validity", length = 4)
    private Double validity;

    private String quotaunittime;

    private Double quotatime;

    @Column(nullable = false)
    private String quotatype;

    private Double offerprice;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(nullable = false, length = 40, columnDefinition = "varchar(255) default 'NORMAL'")
    private String mode;

    @Column(name = "unitsofvalidity", nullable = false, length = 40, columnDefinition = "varchar(100) default 'Days'")
    private String unitsOfValidity;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "productId")
    private Long productId;
    
    @Column(name = "bandwidth")
    private String bandwidth;

    @Transient
    private List<Productplanmappingdto> productplanmappingList = new ArrayList<>();

    @Column(name="apig_plan_id")
    private Long apiGatewayPlanId;

    @Column(name = "DESCRIPTION", nullable = false)
    private String desc;

    @Column(name = "service_area_ids")
    private String serviceAreaIds;

    @Column(name = "quotaunit", length = 40)
    private String quotaUnit;

    @Column(name = "quotarestinterval", length = 40)
    private String quotaResetInterval;

    @Column(name = "timebasepolicyid", length = 40)
    private Integer timebasepolicyId;

    @Column(name = "qospolicyid", length = 40)
    private Integer qospolicyId;

    public PostpaidPlan(PostpaidPlanPojo postpaidPlanPojo){
        setApiGatewayPlanId(postpaidPlanPojo.getId().longValue());
        setId(postpaidPlanPojo.getId());
        setName(postpaidPlanPojo.getName());
        setDisplayName(postpaidPlanPojo.getDisplayName());
        setCategory(postpaidPlanPojo.getCategory());
        setDesc(postpaidPlanPojo.getDesc());
        setStartDate(postpaidPlanPojo.getStartDate());
        setEndDate(postpaidPlanPojo.getEndDate());
        setQuota(postpaidPlanPojo.getQuota());
        setStatus(postpaidPlanPojo.getStatus());
        setMvnoId(postpaidPlanPojo.getMvnoId());
        setPlantype(postpaidPlanPojo.getPlantype());
        setPlanGroup(postpaidPlanPojo.getPlanGroup());
        setValidity(postpaidPlanPojo.getValidity());
        setQuotaunittime(postpaidPlanPojo.getQuotaunittime());
        setQuotatime(postpaidPlanPojo.getQuotatime());
        setQuotatype(postpaidPlanPojo.getQuotatype());
        setOfferprice(postpaidPlanPojo.getOfferprice());
        setIsDelete(postpaidPlanPojo.getIsDelete());
        setMode(postpaidPlanPojo.getMode());
        setUnitsOfValidity(postpaidPlanPojo.getUnitsOfValidity());
        setBuId(postpaidPlanPojo.getBuId());
        setProductId(postpaidPlanPojo.getProductId());
        setBandwidth(postpaidPlanPojo.getBandwidth());
        setQuotaUnit(postpaidPlanPojo.getQuotaUnit());
        setTimebasepolicyId(postpaidPlanPojo.getTimebasepolicyId());
        setQospolicyId(postpaidPlanPojo.getQospolicyId());
        setQuotaResetInterval(postpaidPlanPojo.getQuotaResetInterval());
        if(postpaidPlanPojo.getServiceAreaIds()!=null && !postpaidPlanPojo.getServiceAreaIds().isEmpty()) {
            String serviceAreaIds = "";
            for (Long longValue : postpaidPlanPojo.getServiceAreaIds()) {
                serviceAreaIds = serviceAreaIds.concat("," + longValue.toString());
            }
            setServiceAreaIds(serviceAreaIds);
        }

    }

}

