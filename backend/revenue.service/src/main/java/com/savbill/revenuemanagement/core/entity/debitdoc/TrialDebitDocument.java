package com.savbill.revenuemanagement.core.entity.debitdoc;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLTTRIALDEBITDOCUMENT")
public class TrialDebitDocument {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trialdebitdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "trialdebitdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriberid")
    private Customers customer;

    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;

    @Column(name = "createdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdate;

    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;
    @DiffIgnore
    @Column(name = "duedate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime duedate;
    @DiffIgnore
    @Column(name = "latepaymentdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime latepaymentdate;
    @DiffIgnore
    @Column(name = "subtotal", nullable = false, length = 40)
    private double subtotal;
    @DiffIgnore
    @Column(name = "tax", nullable = false, length = 40)
    private double tax;
    @DiffIgnore
    @Column(name = "discount", nullable = false, length = 40)
    private double discount;
    @DiffIgnore
    @Column(name = "totalamount", nullable = false, length = 40)
    private double totalamount;
    @DiffIgnore
    @Column(name = "previousbalance", nullable = false, length = 40)
    private double previousbalance;
    @DiffIgnore
    @Column(name = "latepaymentfee", nullable = false, length = 40)
    private double latepaymentfee;
    @DiffIgnore
    @Column(name = "currentpayment", nullable = false, length = 40)
    private double currentpayment;
    @DiffIgnore
    @Column(name = "currentdebit", nullable = false, length = 40)
    private double currentdebit;
    @DiffIgnore
    @Column(name = "currentcredit", nullable = false, length = 40)
    private double currentcredit;
    @DiffIgnore
    @Column(name = "totaldue", nullable = false, length = 40)
    private double totaldue;
    @DiffIgnore
    @Column(name = "totalamountinwords", nullable = false, length = 40)
    private String amountinwords;
    @DiffIgnore
    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String dueinwords;
    @DiffIgnore
    @Column(name = "trialbillrunid", nullable = false, length = 40)
    private Integer billrunid;
    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;
    @DiffIgnore
    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @DiffIgnore
    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;
    @DiffIgnore
    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;
    @DiffIgnore
    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;

    @Column(name = "adjustedamount")
    private Double adjustedAmount;

    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    @Column(name = "paymentstatus", length = 40)
    private String paymentStatus;
//    @OneToOne
//    @JoinColumn(name = "purchaseorder_id")
//    private PurchaseOrder purchaseorder;
@DiffIgnore
    @Column(name = "billable_to_name")
    private String billableToName;
    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TrialDebitDocumentDetail.class)
    @JoinColumn(name = "trialdebitdocumentid")
    private List<TrialDebitDocumentDetail> trialDebitDocumentDetails;


    @DiffIgnore
    @OneToMany(fetch = FetchType.EAGER, targetEntity = DebitDocumentTAXRel.class)
    @ToString.Exclude
    @JoinColumn(name = "debitdocumentid")
    private List<TrialDebitDocumentTAXRel> trialDebitDocumentTAXRels;
}
