package com.savbill.revenuemanagement.core.service.partner;


import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblmpartnerpayment")
public class PartnerPayment implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerpaymentid")
    private Long id;

    private String transcategory;

    @Column(name = "payment_mode")
    private String paymentmode;

    private String refno;
    private String orderid;
    private String paymentstatus;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id")
    @ToString.Exclude
    private Partner partner;

    private Double amount = 0.0;
    private Double credit=0.0;
    private String chequenumber;

    private LocalDate chequedate;
    private String remarks;

    private LocalDate paymentdate;
    private String bank_name;
    private String branch_name;

    @Column(name = "next_team_hir_mapping_id")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Transient
    private String partnerName;

    @Column(name = "online_source")
    private String onlinesource;

    @Column(name = "source_bank")
    private Long sourceBank;

    @Column(name = "destination_bank")
    private Long destinationBank;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
