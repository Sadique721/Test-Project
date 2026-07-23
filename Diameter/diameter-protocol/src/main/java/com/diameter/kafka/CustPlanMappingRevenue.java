package com.diameter.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CustPlanMappingRevenue {

    private Integer id;

    private Integer planId;
    private String service;
    private String startDate;
    private String endDate;
    private String expiryDate;
    private String status;
    private Integer customerId;
    private Double offerPrice;
    private Double taxAmount;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;
    private Integer billableCustomerId;
    private Boolean isinvoicestop = false;
    private Boolean istrialplan = false;
    private Long debitdocid;
    private Boolean isDelete;
    private  String custrefName;
    private Double discount;
    private Integer planValidityDays;
    private Integer planGroupId;
    private Boolean isInvoiceToOrg;
    private String billTo;
    private Double newAmount;
    private Integer renewalId;
    private Integer custRefId;
    private Integer nextStaff;
    private Double dbr;
    private String custPlanStatus;
    private Integer nextTeamHierarchyMappingId;
    private Boolean isInvoiceCreated;
    private Double oldDiscount;
    private Integer graceDays = 0;
    private String stopServiceDate;
    private Integer custServiceMappingId;
    private String graceDateTime;
    private String invoiceType;
    private Long traildebitdocid;
    private Long promisetopay_renew_count;
    private Double isTrialValidityDays;
    private Integer trialPlanValidityCount =0;
    private String startServiceDate;
    private String serviceHoldDate;
    private String promise_to_pay_startdate;;
    private String promise_to_pay_enddate;
    private Integer totalHoldDays;
    private String discountType="One-time";
    private String discountExpiryDate;
    private String downTimeExpiryDate;
    private String downTimeStartDate;
    private Integer cprIdForPromiseToPay;
    private Boolean isHold;
    private Boolean isVoid;
    private String serviceHoldBy;

    private String serviceStartBy;

    private Boolean isContainsCustomerInvoice;

    private Integer customerCpr;

    private Long creditdocid;

    private Integer serviceId;

    private String serialNumber;
    private Long qosPolicyId;

    public CustPlanMappingRevenue() {
    }
}


