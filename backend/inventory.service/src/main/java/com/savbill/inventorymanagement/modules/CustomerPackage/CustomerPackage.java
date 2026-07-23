package com.savbill.inventorymanagement.modules.CustomerPackage;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tbltcustpackagerel")
public class CustomerPackage extends Auditable<Integer> implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid")
    private Long custPackageId;
    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customers;
    @ManyToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan plan;
    @Column(name = "startdate")
    private LocalDate startDate;
    @Column(name = "enddate")
    private LocalDate endDate;
    @Column(name = "expirydate")
    private LocalDate expiryDate;
    private String status;
    @Column(name = "is_delete")
    private Boolean isDelete;
    @Column(name = "is_invoice_to_org")
    private boolean isInvoiceToOrg;
    @Column(name = "bill_to")
    private String billTo;
    private String service;
    @Column(name = "cust_plan_status")
    private String custPlanStatus;
    @OneToOne
    @JoinColumn(name = "plangroupid")
    private PlanGroup planGroup;
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return custPackageId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
