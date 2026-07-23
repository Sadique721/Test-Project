package com.diameter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "tblcustsmsdtls")
public class CustSmsDetails extends Auditable{

    public CustSmsDetails() {
    }

    // ================= PRIMARY KEY =================

    @Id
    @DiffIgnore
    @Column(name = "smsdtlsid", nullable = false)
    private Long id;   // ✅ bigint → Long

    // ================= RELATIONS =================

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custid", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planid", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PostpaidPlan postpaidPlan;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custpackageid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustPlanMappping custPlanMappping;

    @Transient
    @JsonAlias({"custpackageid", "custPackageId"})
    private Long custPackageId;

    // ================= COLUMNS =================

    @Column(name = "smstype", length = 50)
    private String smsType;

    @Column(name = "totalsms", precision = 20, scale = 4)
    private BigDecimal totalSms = BigDecimal.ZERO;

    @Column(name = "usedsms", precision = 20, scale = 4)
    private BigDecimal usedSms = BigDecimal.ZERO;

    // ================= COPY CONSTRUCTOR =================

    public CustSmsDetails(CustSmsDetails custSmsDetails) {
        this.id = custSmsDetails.getId();
        this.customer = custSmsDetails.getCustomer();
        this.postpaidPlan = custSmsDetails.getPostpaidPlan();
        this.custPlanMappping = custSmsDetails.getCustPlanMappping();
        this.smsType = custSmsDetails.getSmsType();
        this.totalSms = custSmsDetails.getTotalSms();
        this.usedSms = custSmsDetails.getUsedSms();

        super.setCreatedByName(custSmsDetails.getCreatedByName());
        super.setCreatedate(custSmsDetails.getCreatedate());
        super.setCreatedById(custSmsDetails.getCreatedById());
        super.setLastModifiedById(custSmsDetails.getLastModifiedById());
        super.setLastModifiedByName(custSmsDetails.getLastModifiedByName());
        super.setUpdatedate(custSmsDetails.getUpdatedate());
    }

}
