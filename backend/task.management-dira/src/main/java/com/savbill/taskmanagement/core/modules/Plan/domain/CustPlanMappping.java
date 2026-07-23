package com.savbill.taskmanagement.core.modules.Plan.domain;

import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustPlanMappingRevenue;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLCUSTPACKAGEREL")
@EntityListeners(AuditableListener.class)
public class CustPlanMappping extends Auditable {
	

	/*
CREATE TABLE TBLCUSTPACKAGEREL
(
	custpackageid SERIAL PRIMARY KEY,
	custid BIGINT UNSIGNED NOT NULL,
	planid BIGINT UNSIGNED NOT NULL,
	startdate timestamp not null,
	enddate timestamp not null,
	expirydate timestamp not null,
	status char(1),
	FOREIGN KEY(custid) REFERENCES tblcustomers(custid),
	FOREIGN KEY(planid) REFERENCES TBLMPOSTPAIDPLAN(postpaidplanid)
);
 
	 */

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;



    @Column(nullable = false, length = 40)
    private String service;

    @Column(name = "startdate", nullable = false, length = 40)
    private LocalDateTime startDate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Column(name = "expirydate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiryDate;

    @Column(name = "status", nullable = false, length = 150)
    private String status;

    @JsonBackReference
    @JoinColumn(name = "custid")
    private Integer custid;

    @Transient
    private PostpaidPlan postpaidPlan;



//    private String uploadqos;
//
//    private String downloadqos;
//
//    private String uploadts;
//
//    private String downloadts;
//    private Double offerPrice;
//    private Double taxAmount;
//    private Double walletBalUsed = 0.0;
//    private String purchaseType;
//    private Long onlinePurchaseId;
//    private String purchaseFrom;
//
//    @Column(name = "billable_cust_id", nullable = true)
//    private Integer billableCustomerId;




//    @Column(columnDefinition = "Boolean default false",name = "isinvoicestop")
//    private Boolean isinvoicestop = false;
//
//    @Column(columnDefinition = "Boolean default false",name = "istrialplan")
//    private Boolean istrialplan = false;



    private Long debitdocid;

    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @Column(name="service_id")
    private Long serviceId;
    @Column(name = "serial_number")
    private String serialNumber;
//
//    @Transient
//    private Double validity;
//
//    @Column(name = "discount")
//    private Double discount;
//
//    @Column(name = "plan_validity_days")
//    private Integer planValidityDays;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;
    
    @Column(name = "bill_to")
    private String billTo;

//    @Column(name = "new_amount")
//    private Double newAmount;
//
//    @Column(name = "renewal_id")
//    private Integer renewalId;
//
//    @Column(name = "cust_ref_id")
//    private Integer custRefId;
//
//    @Column(name = "next_staff")
//    private Integer nextStaff;
//
//    @Column(name = "cust_ref_name")
//    private String custRefName;
//
//    @Column(name = "dbr")
//    private Double dbr;
//
    @Column(name = "cust_plan_status")
    private String custPlanStatus;
//
//    @Column(name = "next_team_hir_mapping")
//    private Integer nextTeamHierarchyMappingId;
//
//    @Column(name = "is_invoice_created")
//    private Boolean isInvoiceCreated;
//
//    @Column(name = "old_discount")
//    private Double oldDiscount;
//
//    @Column(name = "grace_days")
//    private Integer graceDays = 0;
//
//    @Column(name = "stop_service_date", length = 40)
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDate stopServiceDate;
//
//    @Column(name = "custservicemappingid", nullable = false)
//    private Integer custServiceMappingId;
//
//    @Column(name = "remarks")
//    private String remarks;
//
//    @Column(name = "ezybill_service_id")
//    private String ezyBillServiceId;
//    @Column(name = "ezbill_package_id")
//    private String ezBillPackageId;
//    @Column(name = "cas_id")
//    private String casId;
//
//    @Column(name = "grace_date_time")
//    private LocalDateTime graceDateTime;


//    @Column(name = "invoice_type")
//    private String invoiceType;
//
//    private Long traildebitdocid;
//
//    @Column(name = "promise_to_pay_remarks")
//    private String promise_to_pay_remarks;
//
//    @Column(name = "promisetopay_renew_count")
//    private Long promisetopay_renew_count;
//
//
//    @Column(name = "is_trial_validity", length = 4)
//    private Double isTrialValidityDays;
//
//    @Column(name = "trial_plan_validity_count")
//    private Integer trialPlanValidityCount =0;
//
//    @Column(name = "start_servicedate", nullable = false, length = 40)
//    private LocalDateTime startServiceDate;
//
//    @Column(name = "service_stop_date", nullable = false, length = 40)
//    private LocalDateTime serviceHoldDate;
//
//
//
//    @Column(name = "promise_to_pay_startdate", length = 40)
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDateTime promise_to_pay_startdate;;
//
//    @Column(name = "promise_to_pay_enddate", length = 40)
//    private LocalDateTime promise_to_pay_enddate;
//
//    @Column(name = "total_hold_days")
//    private Integer totalHoldDays;
//
//    @Column(name = "s_discount_type")
//    private String discountType="One-time";
//
//    @Column(name = "discount_expiry_date")
//    private LocalDate discountExpiryDate;


//    @Column(name = "downtime_expiry_date")
//    private LocalDate downTimeExpiryDate;
//
//    @Column(name = "downtime_start_date")
//    private LocalDate downTimeStartDate;
//
//    @Column(name = "cprid_promise")
//    private Integer cprIdForPromiseToPay;
//
//    @Column(name = "is_hold")
//    private Boolean isHold;
//
//    @Column(name = "is_void")
//    private Boolean isVoid;

//    @Column(name = "extend_validity_remarks")
//    private String extendValidityremarks;

//    @Column(name = "service_hold_by")
//    private String serviceHoldBy;
//
//    @Column(name = "service_start_by")
//    private String serviceStartBy;
//
//    @Column(name = "service_hold_remarks")
//    private String serviceHoldRemarks;
//
//    @Column(name = "service_start_remarks")
//    private String serviceStartRemarks;
//
//    @Column(name = "is_contains_cust_invoice")
//    private Boolean isContainsCustomerInvoice;

//    @Column(name = "cust_cpr")
//    private Integer customerCpr;

    public CustPlanMappping() {
    }

    public CustPlanMappping(CustPlanMappping custPlanMappping) {
        this.expiryDate = custPlanMappping.getExpiryDate();
    }

    private Long creditdocid;
    @Transient
    private  boolean isServiceThroughLead;


    public CustPlanMappping(Integer id, String serialNumber) {
        this.id = id;
        this.serialNumber = serialNumber;
    }

    public CustPlanMappping(CustPlanMappingRevenue custPlanMapppingList) {
        this.id = custPlanMapppingList.getId();
        this.custid = custPlanMapppingList.getCustomerId();
        this.planId = custPlanMapppingList.getPlanId();
        this.billTo = custPlanMapppingList.getBillTo();
        this.isInvoiceToOrg = custPlanMapppingList.getIsInvoiceToOrg();
        this.service = custPlanMapppingList.getService();
        this.custPlanStatus = custPlanMapppingList.getCustPlanStatus();
        this.isDelete = custPlanMapppingList.getIsDelete();
    }

    @Override
    public String toString() {
        return "CustPlanMappping{}";
    }
}
