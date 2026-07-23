package com.savbill.revenuemanagement.core.entity.customers;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblcustquotadtls")
@EntityListeners(AuditableListener.class)
public class CustQuotaDetails extends Auditable {
	
    /*
	create table tblcustquotadtls
	(
		quotadtlsid serial primary key,
		custid BIGINT UNSIGNED NOT NULL,
		planid BIGINT UNSIGNED NOT NULL,
		quotatype varchar(50),
		totalquota numeric(20,4),
		usedquota numeric(20,4),
		created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
		lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
		foreign key (custid) references tblcustomers(custid),
		foreign key (planid) references TBLMPOSTPAIDPLAN(POSTPAIDPLANID)
	);
	 
*/

    public CustQuotaDetails() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotadtlsid", nullable = false, length = 40)
    private Integer id;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;

    @Column(name = "quotatype")
    private String quotaType;

    @Column(name = "totalquota")
    private Double totalQuota = 0.0;

    @Column(name = "usedquota")
    private Double usedQuota = 0.0;

    @Column(name = "quotaunit")
    private String quotaUnit;

    @Column(name = "timetotalquota")
    private Double timeTotalQuota = 0.0;

    @Column(name = "timequotaused")
    private Double timeQuotaUsed = 0.0;

    @Column(name = "timequotaunit")
    private String timeQuotaUnit;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @Column(name = "totalquotakb")
    private Double totalQuotaKB = 0.0;

    @Column(name = "usedquotakb")
    private Double usedQuotaKB = 0.0;

    @Column(name = "timeusedquotasec")
    private Double timeUsedQuotaSec = 0.0;

    @Column(name = "timetotalquotasec")
    private Double timeTotalQuotaSec = 0.0;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custpackageid")
//    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustPlanMappping custPlanMappping;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "custid")
    private Customers customer;

    private Double didtotalquota;
    private Double didusedquota;
    private Double intercomtotalquota;
    private Double intercomusedquota;
    private String didQuotaUnit;
    private String intercomQuotaUnit;

    public CustQuotaDetails(CustQuotaDetails custQuotaDetails) {
        this.id = custQuotaDetails.getId();
        this.postpaidPlan = custQuotaDetails.getPostpaidPlan();
        this.quotaType = custQuotaDetails.getQuotaType();
        this.totalQuota = custQuotaDetails.getTotalQuota();
        this.usedQuota = custQuotaDetails.getUsedQuota();
        super.setCreatedByName(custQuotaDetails.getCreatedByName());
        super.setCreatedate(custQuotaDetails.getCreatedate());
        super.setCreatedById(custQuotaDetails.getCreatedById());
        super.setLastModifiedById(custQuotaDetails.getLastModifiedById());
        super.setLastModifiedByName(custQuotaDetails.getLastModifiedByName());
        super.setUpdatedate(custQuotaDetails.getUpdatedate());
        this.quotaUnit = custQuotaDetails.getQuotaUnit();
        this.timeTotalQuota = custQuotaDetails.getTimeTotalQuota();
        this.timeQuotaUsed = custQuotaDetails.getTimeQuotaUsed();
        this.timeQuotaUnit = custQuotaDetails.getTimeQuotaUnit();
        this.totalQuotaKB = custQuotaDetails.getTotalQuotaKB();
        this.usedQuotaKB = custQuotaDetails.getUsedQuotaKB();
        this.timeUsedQuotaSec = custQuotaDetails.getTimeUsedQuotaSec();
        this.timeTotalQuotaSec = custQuotaDetails.getTimeTotalQuotaSec();
        this.custPlanMappping = custQuotaDetails.getCustPlanMappping();
        this.customer = custQuotaDetails.getCustomer();
    }
}
