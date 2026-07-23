package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.CustQuotaDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustQuotaDtlsPojo {

	private Integer id;

    private Integer planId;

    private String quotaType;

    private Double totalQuota;

    private Double usedQuota;

    private String quotaUnit;

    private Double timeTotalQuota;

    private Double timeQuotaUsed;

    private String timeQuotaUnit;

    private Boolean isDelete = false;

    private Double totalQuotaKB;

    private Double usedQuotaKB;

    private Double timeUsedQuotaSec;

    private Double timeTotalQuotaSec;

    private Double didtotalquota;

    private Double didusedquota;

    private Double intercomtotalquota;

    private Double intercomusedquota;

    private String didQuotaUnit;

    private String intercomQuotaUnit;
    
    private String planName;

	private Long leadMasterId;
    
    private Integer custPlanMapppingId;
    
    public CustQuotaDtlsPojo(CustQuotaDtls custQuotaDtls) {
    	this.id = custQuotaDtls.getId();
    	this.planId = custQuotaDtls.getPlanId();
    	this.quotaType = custQuotaDtls.getQuotaType();
    	this.totalQuota = custQuotaDtls.getTotalQuota();
    	this.usedQuota = custQuotaDtls.getUsedQuota();
    	this.quotaUnit = custQuotaDtls.getQuotaUnit();
    	this.timeTotalQuota = custQuotaDtls.getTimeTotalQuota();
    	this.timeQuotaUsed = custQuotaDtls.getTimeQuotaUsed();
    	this.timeQuotaUnit = custQuotaDtls.getTimeQuotaUnit();
    	this.isDelete =  custQuotaDtls.getIsDelete();
    	this.totalQuotaKB = custQuotaDtls.getTotalQuotaKB();
    	this.usedQuotaKB = custQuotaDtls.getUsedQuotaKB();
    	this.timeTotalQuotaSec = custQuotaDtls.getTimeTotalQuotaSec();
    	this.timeUsedQuotaSec = custQuotaDtls.getTimeUsedQuotaSec();
    	this.didtotalquota = custQuotaDtls.getDidtotalquota();
    	this.didusedquota = custQuotaDtls.getDidusedquota();
    	this.intercomtotalquota = custQuotaDtls.getIntercomtotalquota();
    	this.intercomusedquota = custQuotaDtls.getIntercomusedquota();
    	this.didQuotaUnit = custQuotaDtls.getDidQuotaUnit();
    	this.intercomQuotaUnit = custQuotaDtls.getIntercomQuotaUnit();
        this.planName = custQuotaDtls.getPlanName();
    	if(custQuotaDtls.getLeadMaster() != null) {
    		this.leadMasterId = custQuotaDtls.getLeadMaster().getId();
    	}
    	if(custQuotaDtls.getCustPlanMappping() != null) {
    		this.custPlanMapppingId = custQuotaDtls.getCustPlanMappping().getId();
    	}
    }
}
