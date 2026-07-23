package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustQuotaDtlsPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCUSTQUOTADTLS")
public class CustQuotaDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_quota_dtls_id", nullable = false)
	private Integer id;

    private Integer planId;

    private String quotaType;

    private Double totalQuota;

    private Double usedQuota;

    private String quotaUnit;

    private Double timeTotalQuota;

    private Double timeQuotaUsed;

    private String timeQuotaUnit;

    private Boolean isDelete;

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

    @JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_plan_mapping_id")
    private CustPlanMappping custPlanMappping;
    
    
    public CustQuotaDtls(CustQuotaDtlsPojo custQuotaDtlsPojo) {
    	this.id = custQuotaDtlsPojo.getId();
    	this.planId = custQuotaDtlsPojo.getPlanId();
    	this.quotaType = custQuotaDtlsPojo.getQuotaType();
    	this.totalQuota = custQuotaDtlsPojo.getTotalQuota();
    	this.usedQuota = custQuotaDtlsPojo.getUsedQuota();
    	this.quotaUnit = custQuotaDtlsPojo.getQuotaUnit();
    	this.timeTotalQuota = custQuotaDtlsPojo.getTimeTotalQuota();
    	this.timeQuotaUsed = custQuotaDtlsPojo.getTimeQuotaUsed();
    	this.timeQuotaUnit = custQuotaDtlsPojo.getTimeQuotaUnit();
    	this.isDelete =  custQuotaDtlsPojo.getIsDelete();
    	this.totalQuotaKB = custQuotaDtlsPojo.getTotalQuotaKB();
    	this.usedQuotaKB = custQuotaDtlsPojo.getUsedQuotaKB();
    	this.timeTotalQuotaSec = custQuotaDtlsPojo.getTimeTotalQuotaSec();
    	this.timeUsedQuotaSec = custQuotaDtlsPojo.getTimeUsedQuotaSec();
    	this.didtotalquota = custQuotaDtlsPojo.getDidtotalquota();
    	this.didusedquota = custQuotaDtlsPojo.getDidusedquota();
    	this.intercomtotalquota = custQuotaDtlsPojo.getIntercomtotalquota();
    	this.intercomusedquota = custQuotaDtlsPojo.getIntercomusedquota();
    	this.didQuotaUnit = custQuotaDtlsPojo.getDidQuotaUnit();
    	this.intercomQuotaUnit = custQuotaDtlsPojo.getIntercomQuotaUnit();
        this.planName = custQuotaDtlsPojo.getPlanName();
    	if(custQuotaDtlsPojo.getLeadMasterId() != null) {
    		this.leadMaster = new LeadMaster(custQuotaDtlsPojo.getLeadMasterId());
    	}
    	if(custQuotaDtlsPojo.getCustPlanMapppingId() != null) {
    		this.custPlanMappping = new CustPlanMappping(custQuotaDtlsPojo.getCustPlanMapppingId());
    	}
    }
}
