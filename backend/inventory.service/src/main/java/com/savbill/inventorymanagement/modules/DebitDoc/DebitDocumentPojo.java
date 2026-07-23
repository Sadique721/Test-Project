package com.savbill.inventorymanagement.modules.DebitDoc;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DebitDocumentPojo extends Auditable {

    private Integer id;

    private String docnumber;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

    private Integer planId;

    private LocalDateTime billdate;

    private LocalDateTime startdate;

    private LocalDateTime endate;

    private LocalDateTime duedate;

    private LocalDateTime latepaymentdate;

    private Double subtotal;

    private Double tax;

    private Double discount;

    private Double totalamount;

    private Double previousbalance;

    private Double latepaymentfee;

    private Double currentpayment;

    private Double currentdebit;

    private Double currentcredit;

    private Double totaldue;

    private String amountinwords;

    private String dueinwords;

    private Integer billrunid;

    private String billrunstatus;

    private String document;

    private Boolean isDelete = false;

    private Long cstchargeid;
    
    private Integer custid;

    private String customerName;

    private String custType;

    private String paymentStatus;

    private Double adjustedAmount;

//    private List<CreditDocument> creditDocumentList;

    private String custRefName;

    private  String refundAbleAmount;

//    private List<DebitDocumentTAXRel> debitDocumentTAXRels;
    private Integer nextStaff;
    private Integer nextTeamHierarchyMappingId;

    private String status;

//    private List<DebitDocDetails> debitDocDetails;

    private Boolean  isDirectChargeInvoice;

    private Integer lcoId;

    private String paymentowner;
//    private PurchaseOrder purchaseorder;

    private String billableToName;

//    private List<DebitDocumentInventoryRel> debitDocumentInventoryRels;

    private Boolean isPromiseToPayInOldCPR;

    private String promiseToPayHoldDays;

    private LocalDate promiseStartDate;

    private LocalDate promiseEndDate;

    private Boolean isCNEnable;

    private String invoiceCancelRemarks;

    private Double pendingAmt = 0d;
}
