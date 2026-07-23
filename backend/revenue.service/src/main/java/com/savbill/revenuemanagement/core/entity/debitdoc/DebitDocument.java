package com.savbill.revenuemanagement.core.entity.debitdoc;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
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
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "debitdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriberid")
    @ToString.Exclude
    private Customers customer;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;

    @DiffIgnore
    @CreationTimestamp
    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;

    @DiffIgnore
    @Column(name = "localbilldate")
    private String debitDocument;

//    @CreationTimestamp
    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @DiffIgnore
    @Column(name = "localstartdate")
    private String localstartdate;

//    @CreationTimestamp
    @DiffIgnore
    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    @DiffIgnore
    @Column(name = "localenddate")
    private String localenddate;

    @DiffIgnore
    @Column(name = "duedate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime duedate;

    @DiffIgnore
    @CreationTimestamp
    @Column(name = "latepaymentdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime latepaymentdate;

    @DiffIgnore
    @Column(name = "subtotal", nullable = false, length = 40)
    private Double subtotal;

    @DiffIgnore
    @Column(name = "tax", nullable = false, length = 40)
    private Double tax;

    @DiffIgnore
    @Column(name = "installment_interest", nullable = false, length = 40)
    private Double installmentInterest;

    @DiffIgnore
    @Column(name = "discount", nullable = false, length = 40)
    private Double discount;

    @DiffIgnore
    @Column(name = "totalamount", nullable = false, length = 40)
    private Double totalamount;

    @DiffIgnore
    @Column(name = "previousbalance", nullable = false, length = 40)
    private Double previousbalance;

    @DiffIgnore
    @Column(name = "latepaymentfee", nullable = false, length = 40)
    private Double latepaymentfee;

    @DiffIgnore
    @Column(name = "currentpayment", nullable = false, length = 40)
    private Double currentpayment;

    @DiffIgnore
    @Column(name = "currentdebit", nullable = false, length = 40)
    private Double currentdebit;

    @DiffIgnore
    @Column(name = "currentcredit", nullable = false, length = 40)
    private Double currentcredit;

    @DiffIgnore
    @Column(name = "totaldue", nullable = false, length = 40)
    private Double totaldue;

    @DiffIgnore
    @Column(name = "totalamountinwords", nullable = false, length = 40)
    private String totalamountinwords;

    @DiffIgnore
    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String totaldueinwords;

    @DiffIgnore
    @Column(name = "billrunid", nullable = false, length = 40)
    private Integer billrunid;

    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;

    @DiffIgnore
    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    private Long cstchargeid;

    Boolean is_credit_reversal;

    Integer credit_doc_id;

    @DiffIgnore
    @Column(name = "payment_status", nullable = false, length = 40)
    private String paymentStatus;

    @DiffIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tbltcreditdebitmapping", joinColumns = @JoinColumn(name = "debitdocumentid"), inverseJoinColumns = @JoinColumn(name = "CREDITDOCID"))
    @JsonIgnoreProperties("debitDocumentList")
    @ToString.Exclude
    private List<CreditDocument> creditDocumentList;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;

    @DiffIgnore
    @Column(name = "totalcustomerdiscount")
    private Double totalCustomerDiscount;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "cust_ref_name")
    private String custRefName;

    @DiffIgnore
    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    @DiffIgnore
    @OneToMany(fetch = FetchType.EAGER, targetEntity = DebitDocumentTAXRel.class)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocumentTAXRel> debitDocumentTAXRels;

    @DiffIgnore
    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;

    @DiffIgnore
    @Column(name = "next_staff")
    private Integer nextStaff;
    @Column(name = "next_team_hir_mapping_id")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "status")
    private String  status;

    @DiffIgnore
    @Column(name = "is_direct_invoice")
    private Boolean  isDirectChargeInvoice;

    @DiffIgnore
    @Column(name = "lcoid")
    private Integer lcoId;

    @DiffIgnore
    @Column(name ="paymentowner")
    private String paymentowner;

//    @OneToOne
//    @JoinColumn(name = "purchaseorder_id")
//    private PurchaseOrder purchaseorder;

    @DiffIgnore
    @Transient
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocDetails.class)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocDetails> debitDocDetailsList;

    @DiffIgnore
    @Column(name ="billable_to_name")
    private String billableToName;

//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocumentInventoryRel.class)
//    @JoinColumn(name = "debitdocumentid")
//    private List<DebitDocumentInventoryRel> debitDocumentInventoryRels;

    @DiffIgnore
    @Column(name ="staffid")
    private Integer staffid;

    @DiffIgnore
    @Column(name ="is_contains_promise")
    private Boolean isPromiseToPayInOldCPR;

    @DiffIgnore
    @Column(name ="promise_pay_hold_days")
    private String promiseToPayHoldDays;

    @DiffIgnore
    @Column(name ="promise_start_date")
    private LocalDate promiseStartDate;

    @DiffIgnore
    @Column(name ="promise_end_date")
    private LocalDate promiseEndDate;

    @DiffIgnore
    @Column(name ="is_cn_enable")
    private Boolean isCNEnable;

    @Column(name = "invoice_cancel_remarks")
    private String invoiceCancelRemarks;

    @Column(name = "remarks")
    private String remarks;

    @DiffIgnore
    @Column(name = "email")
    private String email;

    @DiffIgnore
    @Column(name = "firstbill")
    private String firstbill;

    @DiffIgnore
    @Column(name = "print_counter")
    private Integer printCounter;
    @DiffIgnore
    @Column(name ="last_reprint_date")
    private LocalDateTime lastReprintDate;
    @DiffIgnore
    @Transient
    private Double pendingAmt = 0d;
    @Transient
    private String duedateString;
    @Transient
    private String latepaymentdateString;
    @Column(name= "operation_type",length= 100)
    private String operationType;

    @Column(name= "used_by_thread")
    private Boolean usedByThread;

    @Column(name= "isp_payload_status_code")
    private String ispPayloadStatusCode;

    @Column(name= "qr_code")
    private String qrCode;

    @Column(name= "debitdoc_grace_days")
    private  Integer debitDocGraceDays;

    @Transient
    private String mvnoName;

    @Transient
    private List<Integer> updateDebitDpcDetailsIds;
    @Column(name = "krainvoice_id")
    private String kraInvoiceId;
    @Column(name = "is_kra_synced", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private Boolean isKraSynced = false;
    @Column(name = "cur_recpt_no")
    private Long curRecptNo;

    @Column(name = "tot_recpt_no")
    private Long totRecptNo;

    @Column(name = "scu_internal_data")
    private String scuInternalData;

    @Column(name = "scu_receipt_signature")
    private String scuReceiptSignature;

    @Column(name = "sdcid")
    private String sdcid;

    @Column(name = "sdcmrc_no")
    private String sdcmrcNo;

    @Column(name = "sdc_date_time")
    private LocalDateTime sdcDateTime;

    @Column(name = "is_stock_io")
    private Boolean isStockIO;


}
