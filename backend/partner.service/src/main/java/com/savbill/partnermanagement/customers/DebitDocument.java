package com.savbill.partnermanagement.customers;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLTDEBITDOCUMENT")
@EntityListeners(AuditableListener.class)
public class DebitDocument extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "debitdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriberid")
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;

    @CreationTimestamp
    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;

    @Column(name = "localbilldate")
    private String debitDocument;

    @CreationTimestamp
    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @Column(name = "localstartdate")
    private String localstartdate;

//    @CreationTimestamp
    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    @Column(name = "localenddate")
    private String localenddate;

    @CreationTimestamp
    @Column(name = "duedate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime duedate;

    @CreationTimestamp
    @Column(name = "latepaymentdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime latepaymentdate;

    @Column(name = "subtotal", nullable = false, length = 40)
    private Double subtotal;

    @Column(name = "tax", nullable = false, length = 40)
    private Double tax;

    @Column(name = "discount", nullable = false, length = 40)
    private Double discount;

    @Column(name = "totalamount", nullable = false, length = 40)
    private Double totalamount;

    @Column(name = "previousbalance", nullable = false, length = 40)
    private Double previousbalance;

    @Column(name = "latepaymentfee", nullable = false, length = 40)
    private Double latepaymentfee;

    @Column(name = "currentpayment", nullable = false, length = 40)
    private Double currentpayment;

    @Column(name = "currentdebit", nullable = false, length = 40)
    private Double currentdebit;

    @Column(name = "currentcredit", nullable = false, length = 40)
    private Double currentcredit;

    @Column(name = "totaldue", nullable = false, length = 40)
    private Double totaldue;

    @Column(name = "totalamountinwords", nullable = false, length = 40)
    private String totalamountinwords;

    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String totaldueinwords;

    @Column(name = "billrunid", nullable = false, length = 40)
    private Integer billrunid;

    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;

    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    private Long cstchargeid;

    Boolean is_credit_reversal;

    Integer credit_doc_id;

    @Column(name = "payment_status", nullable = false, length = 40)
    private String paymentStatus;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tbltcreditdebitmapping", joinColumns = @JoinColumn(name = "debitdocumentid"), inverseJoinColumns = @JoinColumn(name = "CREDITDOCID"))
    @JsonIgnoreProperties("debitDocumentList")
    @ToString.Exclude
    private List<CreditDocument> creditDocumentList;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;

    @Column(name = "totalcustomerdiscount")
    private Double totalCustomerDiscount;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "cust_ref_name")
    private String custRefName;

    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = DebitDocumentTAXRel.class)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocumentTAXRel> debitDocumentTAXRels;

    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;
    @Column(name = "next_staff")
    private Integer nextStaff;
    @Column(name = "next_team_hir_mapping_id")
    private Integer nextTeamHierarchyMappingId;
    @Column(name = "status")
    private String  status;

    @Column(name = "is_direct_invoice")
    private Boolean  isDirectChargeInvoice;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name ="paymentowner")
    private String paymentowner;

//    @OneToOne
//    @JoinColumn(name = "purchaseorder_id")
//    private PurchaseOrder purchaseorder;

    @Transient
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocDetails.class)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocDetails> debitDocDetailsList;

    @Column(name ="billable_to_name")
    private String billableToName;

//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocumentInventoryRel.class)
//    @JoinColumn(name = "debitdocumentid")
//    private List<DebitDocumentInventoryRel> debitDocumentInventoryRels;

    @Column(name ="staffid")
    private Integer staffid;

    @Column(name ="is_contains_promise")
    private Boolean isPromiseToPayInOldCPR;

    @Column(name ="promise_pay_hold_days")
    private String promiseToPayHoldDays;

    @Column(name ="promise_start_date")
    private LocalDate promiseStartDate;

    @Column(name ="promise_end_date")
    private LocalDate promiseEndDate;

    @Column(name ="is_cn_enable")
    private Boolean isCNEnable;

    @Column(name = "invoice_cancel_remarks")
    private String invoiceCancelRemarks;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "email")
    private String email;

    @Column(name = "firstbill")
    private String firstbill;

    @Column(name = "print_counter")
    private Integer printCounter;

    @Column(name ="last_reprint_date")
    private LocalDateTime lastReprintDate;

    @Transient
    private Double pendingAmt = 0d;
    @Transient
    private String duedateString;
    @Transient
    private String latepaymentdateString;
}
