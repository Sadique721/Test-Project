package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostpaidPlanPojo {

    private Integer id;
    private String name;
    private String displayName;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long quota;
    private String status;
    private Integer mvnoId;
    private String plantype;
    private String planGroup;
    private Double validity;
    private String quotaunittime;
    private Double quotatime;
    private String quotatype;
    private Double offerprice;
    private Boolean isDelete = false;
    private String mode;
    private String unitsOfValidity;
    private Long buId;
    private Long productId;
    private String bandwidth;
    private String desc;
    private String quotaUnit;
    private String quotaResetInterval;
    private Integer timebasepolicyId;
    private Integer qospolicyId;

    @JsonManagedReference
    private List<PostpaidPlanChargePojo> chargeList = new ArrayList<>();

    private List<Productplanmappingdto> productplanmappingList = new ArrayList<>();

    private List<Long> serviceAreaIds = new ArrayList<>();

    public PostpaidPlanPojo(PostpaidPlan postpaidPlan){
        setId(postpaidPlan.getApiGatewayPlanId().intValue());
        setName(postpaidPlan.getName());
        setDisplayName(postpaidPlan.getDisplayName());
        setCategory(postpaidPlan.getCategory());
        setDesc(postpaidPlan.getDesc());
        setStartDate(postpaidPlan.getStartDate());
        setEndDate(postpaidPlan.getEndDate());
        setQuota(postpaidPlan.getQuota());
        setStatus(postpaidPlan.getStatus());
        setMvnoId(postpaidPlan.getMvnoId());
        setPlantype(postpaidPlan.getPlantype());
        setPlanGroup(postpaidPlan.getPlanGroup());
        setValidity(postpaidPlan.getValidity());
        setQuotaunittime(postpaidPlan.getQuotaunittime());
        setQuotatime(postpaidPlan.getQuotatime());
        setQuotatype(postpaidPlan.getQuotatype());
        setOfferprice(postpaidPlan.getOfferprice());
        setIsDelete(postpaidPlan.getIsDelete());
        setMode(postpaidPlan.getMode());
        setUnitsOfValidity(postpaidPlan.getUnitsOfValidity());
        setBuId(postpaidPlan.getBuId());
        setProductId(postpaidPlan.getProductId());
        setBandwidth(postpaidPlan.getBandwidth());
        setQuotaUnit(postpaidPlan.getQuotaUnit());
        setTimebasepolicyId(postpaidPlan.getTimebasepolicyId());
        setQospolicyId(postpaidPlan.getQospolicyId());
        setQuotaResetInterval(postpaidPlan.getQuotaResetInterval());
        List<Long> serviceAreaIds = new ArrayList<Long>();
        if(postpaidPlan.getServiceAreaIds()!=null && !postpaidPlan.getServiceAreaIds().isEmpty()){
            for (String s : postpaidPlan.getServiceAreaIds().replaceFirst(",","").split(","))
                serviceAreaIds.add(Long.parseLong(s));
            }
        setServiceAreaIds(serviceAreaIds);
    }



}
