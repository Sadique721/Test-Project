package com.savbill.revenuemanagement.core.entity.customers;


import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustPlanMappingRevenue;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
//import com.savbill.revenuemanagement.productmanagement.qosPolicy.domain.QOSPolicy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLCUSTPACKAGEREL")
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)

public class CustPlanMappping extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

//    @Transient
//    private PostpaidPlanPojo postpaidPlanPojo;

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
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customer;

//    @OneToOne
//    @JoinColumn(name = "qospolicyid")
//    private QOSPolicy qospolicy;
    @OneToOne
    @JoinColumn(name = "plangroupid")
    private PlanGroup planGroup;

    private String uploadqos;

    private String downloadqos;

    private String uploadts;

    private String downloadts;
    private Double offerPrice;
    private Double taxAmount;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;

    @Column(name = "billable_cust_id", nullable = true)
    private Integer billableCustomerId;

    @Column(columnDefinition = "Boolean default false",name = "isinvoicestop")
    private Boolean isinvoicestop = false;

    @Column(columnDefinition = "Boolean default false",name = "istrialplan")
    private Boolean istrialplan = false;

    private Long debitdocid;

    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Transient
    private Double validity; 

    @Column(name = "discount")
    private Double discount;
    
    @Column(name = "plan_validity_days")
    private Integer planValidityDays;

//    @OneToOne
//    @JoinColumn(name = "plangroupid")
//    private PlanGroup planGroup;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;
    
    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @Column(name = "renewal_id")
    private Integer renewalId;

    @Column(name = "cust_ref_id")
    private Integer custRefId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "cust_ref_name")
    private String custRefName;

    @Column(name = "dbr")
    private Double dbr;

    @Column(name = "cust_plan_status")
    private String custPlanStatus;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "is_invoice_created")
    private Boolean isInvoiceCreated;

    @Column(name = "old_discount")
    private Double oldDiscount;

    @Column(name = "grace_days")
    private Integer graceDays = 0;

    @Column(name = "stop_service_date", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate stopServiceDate;

    @Column(name = "custservicemappingid", nullable = false)
    private Integer custServiceMappingId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "ezybill_service_id")
    private String ezyBillServiceId;
    @Column(name = "ezbill_package_id")
    private String ezBillPackageId;
    @Column(name = "cas_id")
    private String casId;

    @Column(name = "grace_date_time")
    private LocalDateTime graceDateTime;

    @Column(name = "invoice_type")
    private String invoiceType;

    private Long traildebitdocid;

    @Column(name = "promise_to_pay_remarks")
    private String promise_to_pay_remarks;

    @Column(name = "promisetopay_renew_count")
    private Long promisetopay_renew_count;


    @Column(name = "is_trial_validity", length = 4)
    private Double isTrialValidityDays;

    @Column(name = "trial_plan_validity_count")
    private Integer trialPlanValidityCount =0;

    @Column(name = "start_servicedate", nullable = false, length = 40)
    private LocalDateTime startServiceDate;

    @Column(name = "service_stop_date", nullable = false, length = 40)
    private LocalDateTime serviceHoldDate;

    @Column(name = "promise_to_pay_startdate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime promise_to_pay_startdate;;

    @Column(name = "promise_to_pay_enddate", length = 40)
    private LocalDateTime promise_to_pay_enddate;

    @Column(name = "total_hold_days")
    private Integer totalHoldDays;
    
    @Column(name = "s_discount_type")
    private String discountType="One-time";

    @Column(name = "discount_expiry_date")
    private LocalDate discountExpiryDate;


    @Column(name = "downtime_expiry_date")
    private LocalDate downTimeExpiryDate;

    @Column(name = "downtime_start_date")
    private LocalDate downTimeStartDate;

    @Column(name = "cprid_promise")
    private Integer cprIdForPromiseToPay;

    @Column(name = "is_hold")
    private Boolean isHold;

    @Column(name = "is_void")
    private Boolean isVoid;

    @Column(name = "extend_validity_remarks")
    private String extendValidityremarks;

    @Column(name = "service_hold_by")
    private String serviceHoldBy;

    @Column(name = "service_start_by")
    private String serviceStartBy;

    @Column(name = "service_hold_remarks")
    private String serviceHoldRemarks;

    @Column(name = "service_start_remarks")
    private String serviceStartRemarks;

    @Column(name = "is_contains_cust_invoice")
    private Boolean isContainsCustomerInvoice;

    @Column(name = "cust_cpr")
    private Integer customerCpr;

    @Column(name="service_id")
    private Integer serviceId;

    @Transient
    private String startDateString;
    @Transient
    private String endDateString;
    @Transient
    private String expiryDateString;

    @Column(name = "renewal_for_booster")
    private Boolean renewalForBooster;

    public CustPlanMappping() {
    }

    public CustPlanMappping(CustPlanMappping custPlanMappping) {
        this.expiryDate = custPlanMappping.getExpiryDate();
    }

    private Long creditdocid;
    @Override
    public String toString() {
        return "CustPlanMappping{}";
    }




    public CustPlanMappping(CustPlanMappingRevenue custPlanMapppingList,Customers customers,String type,PlanGroup planGroup ){


        this.id =custPlanMapppingList.getId();
        this.planId =custPlanMapppingList.getPlanId();
        this.service =custPlanMapppingList.getService();
        this.startDate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getStartDate());
        this.endDate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getEndDate());
        this.expiryDate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getExpiryDate());
//        if (type.equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW) || type.equalsIgnoreCase(Constants.INVOICE_TYPE.ADD_NEW_SERVICE)) {
//            this.startDate = LocalDateTime.parse(custPlanMapppingList.getStartDate().trim(), formatter2);
//            this.endDate =LocalDateTime.parse(custPlanMapppingList.getEndDate().trim(),formatter2);
//            this.expiryDate =LocalDateTime.parse(custPlanMapppingList.getExpiryDate().trim(),formatter2);
//        }else {
//            this.startDate = LocalDateTime.parse(custPlanMapppingList.getStartDate().trim(), formatter);
//            this.endDate =LocalDateTime.parse(custPlanMapppingList.getEndDate().trim(),formatter);
//            this.expiryDate =LocalDateTime.parse(custPlanMapppingList.getExpiryDate().trim(),formatter);
//        }
        this.status =custPlanMapppingList.getStatus();
        this.customer =customers;
        this.offerPrice =custPlanMapppingList.getOfferPrice();
        this.taxAmount =custPlanMapppingList.getTaxAmount();
        this.walletBalUsed =custPlanMapppingList.getWalletBalUsed();
        this.purchaseType =custPlanMapppingList.getPurchaseType();
        this.onlinePurchaseId =custPlanMapppingList.getOnlinePurchaseId();
        this.purchaseFrom =custPlanMapppingList.getPurchaseFrom();
        this.billableCustomerId =custPlanMapppingList.getBillableCustomerId();
        this.isinvoicestop =custPlanMapppingList.getIsinvoicestop();
        this.istrialplan =custPlanMapppingList.getIstrialplan();
        this.debitdocid =custPlanMapppingList.getDebitdocid();
        this.isDelete =custPlanMapppingList.getIsDelete();
        this.discount =custPlanMapppingList.getDiscount();
        this.planValidityDays =custPlanMapppingList.getPlanValidityDays();
//        this.planGroupId =custPlanMapppingList.getPlanGroup().getPlanGroupId();
        this.isInvoiceToOrg =custPlanMapppingList.getIsInvoiceToOrg();
        this.billTo =custPlanMapppingList.getBillTo();
        this.newAmount =custPlanMapppingList.getNewAmount();
        this.renewalId =custPlanMapppingList.getRenewalId();
        this.custRefId =custPlanMapppingList.getCustRefId();
        this.custRefName = custPlanMapppingList.getCustrefName();
        this.nextStaff =custPlanMapppingList.getNextStaff();
        this.dbr =custPlanMapppingList.getDbr();
        this.custPlanStatus =custPlanMapppingList.getCustPlanStatus();
        this.nextTeamHierarchyMappingId =custPlanMapppingList.getNextTeamHierarchyMappingId();
        this.isInvoiceCreated =custPlanMapppingList.getIsInvoiceCreated();
        this.oldDiscount =custPlanMapppingList.getOldDiscount();
        this.graceDays =custPlanMapppingList.getGraceDays();
        if (custPlanMapppingList.getStopServiceDate()!=null) {
            this.stopServiceDate = LocalDate.parse(custPlanMapppingList.getStopServiceDate());
        }
        this.custServiceMappingId =custPlanMapppingList.getCustServiceMappingId();
        if (custPlanMapppingList.getGraceDateTime()!=null) {
            this.graceDateTime = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getGraceDateTime());
        }
        this.invoiceType =custPlanMapppingList.getInvoiceType();
        this.traildebitdocid =custPlanMapppingList.getTraildebitdocid();
        this.promisetopay_renew_count =custPlanMapppingList.getPromisetopay_renew_count();
        this.isTrialValidityDays =custPlanMapppingList.getIsTrialValidityDays();
        this.trialPlanValidityCount =custPlanMapppingList.getTrialPlanValidityCount();
        if (custPlanMapppingList.getStartServiceDate()!=null) {
            this.startServiceDate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getStartServiceDate());
        }
        if (custPlanMapppingList.getServiceHoldDate()!=null) {
            this.serviceHoldDate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getServiceHoldDate());
        }
        if (custPlanMapppingList.getPromise_to_pay_startdate()!=null) {
            this.promise_to_pay_startdate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getPromise_to_pay_startdate());
        }
        if (custPlanMapppingList.getPromise_to_pay_enddate()!=null) {
            this.promise_to_pay_enddate = DateTimeUtil.getLocaldateTimefromString(custPlanMapppingList.getPromise_to_pay_enddate());
        }
        this.totalHoldDays =custPlanMapppingList.getTotalHoldDays();
        this.discountType =custPlanMapppingList.getDiscountType();
        if (custPlanMapppingList.getDiscountExpiryDate()!=null) {
            this.discountExpiryDate = LocalDate.parse(custPlanMapppingList.getDiscountExpiryDate());
            this.downTimeExpiryDate = LocalDate.parse(custPlanMapppingList.getDownTimeExpiryDate());
            this.downTimeStartDate = LocalDate.parse(custPlanMapppingList.getDownTimeStartDate());
        }
        this.cprIdForPromiseToPay =custPlanMapppingList.getCprIdForPromiseToPay();
        this.isHold =custPlanMapppingList.getIsHold();
        this.isVoid =custPlanMapppingList.getIsVoid();
        this.serviceHoldBy =custPlanMapppingList.getServiceHoldBy();
        this.serviceStartBy =custPlanMapppingList.getServiceStartBy();
        this.isContainsCustomerInvoice =custPlanMapppingList.getIsContainsCustomerInvoice();
        this.customerCpr =custPlanMapppingList.getCustomerCpr();
        this.creditdocid =custPlanMapppingList.getCreditdocid();
        this.serviceId =custPlanMapppingList.getServiceId();
        if (custPlanMapppingList.getPlanGroupId()!=null){
            this.planGroup = planGroup;
        }
        this.renewalForBooster = custPlanMapppingList.getRenewalForBooster();
    }
}
