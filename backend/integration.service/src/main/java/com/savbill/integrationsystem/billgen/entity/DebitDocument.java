package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.DebitDocumentMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTDEBITDOCUMENT")
public class DebitDocument {
    @Id
    @Column(name = "debitdocumentid", nullable = false, length = 40)
    private Integer id;
    @Column(name = "debitdocumentnumber", nullable = false, length = 40)
    private String debitdocumentnumber;
    @CreationTimestamp
    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;
    @CreationTimestamp
    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;
    @CreationTimestamp
    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;
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
    private String amountinwords;
    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String dueinwords;
    @Column(name = "billrunid", nullable = false, length = 40)
    private Integer billrunid;
    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    private Long cstchargeid;
    
    @Column(name = "credit_doc_id")
    private Long creditDocId;
    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;
    @Column(name = "status")
    private String status;
    @Column(name = "paymentowner")
    private String paymentowner;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocDetails.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocDetails> debitDocDetailsList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = DebitDocumentTAXRel.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "debitdocumentid")
    private List<DebitDocumentTAXRel> debitDocumentTAXRels;


    @Column(name = "subscriberid")
    private Integer customerId;

    @Column(name = "cust_ref_name")
    private String custRefName;
    
    @Column(name = "ird_sync")
    private String irdSync;
    
    @Column(name = "ird_resp_code")
    private String irdRespCode;

    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    public DebitDocument(DebitDocumentMessage message) {
        this.id = message.getId();
        this.debitdocumentnumber = message.getDocnumber();
        this.billdate = LocalDateTime.parse(message.getBilldate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.startdate = LocalDateTime.parse(message.getStartdate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endate = LocalDateTime.parse(message.getEndate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.duedate = LocalDateTime.parse(message.getDuedate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.latepaymentdate = LocalDateTime.parse(message.getLatepaymentdate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.subtotal = message.getSubtotal();
        this.tax = message.getTax();
        this.discount = message.getDiscount();
        this.totalamount = message.getTotalamount();
        this.previousbalance = message.getPreviousbalance();
        this.latepaymentfee = message.getLatepaymentfee();
        this.currentpayment = message.getCurrentpayment();
        this.currentdebit = message.getCurrentdebit();
        this.currentcredit = message.getCurrentcredit();
        this.totaldue = message.getTotaldue();
        this.amountinwords = message.getAmountinwords();
        this.dueinwords = message.getDueinwords();
        this.billrunid = message.getBillrunid();
        this.billrunstatus = message.getBillrunstatus();
        this.isDelete = message.getIsDelete();
        this.cstchargeid = message.getCstchargeid();
        this.custpackrelid = message.getCustpackrelid();
        this.status = message.getStatus();
        this.paymentowner = message.getPaymentowner();
        this.debitDocumentTAXRels = message.getDebitDocumentTAXRels();
        this.debitDocDetailsList = message.getDebitDocDetailsList();
        this.customerId= message.getCustomerId();
        this.custRefName=message.getCustRefName();
        this.irdRespCode = message.getIrdRespCode();
        this.irdSync = message.getIrdSync();
        this.inventoryMappingId=message.getInventoryMappingId();
    }
}
