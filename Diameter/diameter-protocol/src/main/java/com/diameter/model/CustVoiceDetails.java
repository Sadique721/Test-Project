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
@Table(name = "tblcustvoicedtls")
public class CustVoiceDetails extends Auditable {

    public CustVoiceDetails() {
    }

    @Id
    @DiffIgnore
    @Column(name = "voicedtlsid", nullable = false)
    private Long id;

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

    @Column(name = "voicetype", length = 50)
    private String voiceType;

    @Column(name = "totalvoice", precision = 20, scale = 4)
    private BigDecimal totalVoice = BigDecimal.ZERO;

    @Column(name = "usedvoice", precision = 20, scale = 4)
    private BigDecimal usedVoice = BigDecimal.ZERO;

    @Column(name = "pulse", length = 50)
    private String pulse;

    public CustVoiceDetails(CustVoiceDetails custVoiceDetails) {
        this.id = custVoiceDetails.getId();
        this.customer = custVoiceDetails.getCustomer();
        this.postpaidPlan = custVoiceDetails.getPostpaidPlan();
        this.custPlanMappping = custVoiceDetails.getCustPlanMappping();
        this.voiceType = custVoiceDetails.getVoiceType();
        this.totalVoice = custVoiceDetails.getTotalVoice();
        this.usedVoice = custVoiceDetails.getUsedVoice();
        this.pulse = custVoiceDetails.getPulse();

        super.setCreatedByName(custVoiceDetails.getCreatedByName());
        super.setCreatedate(custVoiceDetails.getCreatedate());
        super.setCreatedById(custVoiceDetails.getCreatedById());
        super.setLastModifiedById(custVoiceDetails.getLastModifiedById());
        super.setLastModifiedByName(custVoiceDetails.getLastModifiedByName());
        super.setUpdatedate(custVoiceDetails.getUpdatedate());
    }

}
