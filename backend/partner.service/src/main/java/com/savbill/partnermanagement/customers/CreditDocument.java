package com.savbill.partnermanagement.customers;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCREDITDOC")
@EntityListeners(AuditableListener.class)
public class CreditDocument extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CREDITDOCID", nullable = false, length = 40)
    private Integer id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    private Customers customer;

    @Column(name = "PAYMODE", nullable = false, length = 40)
    private String paymode;

    @Column(name = "paymentdate", nullable = false, length = 40)
    private LocalDate paymentdate;

    @Column(name = "chequedate", length = 40)
    private LocalDate chequedate;

    @Column(name = "PAYDETAILS1", nullable = false, length = 40)
    private String paydetails1; //Bank

    @Column(name = "PAYDETAILS2", nullable = false, length = 40)
    private String paydetails2; //Branch

    @Column(name = "PAYDETAILS3", nullable = false, length = 40)
    private String paydetails3; //ChequeNo

    @Column(name = "PAYDETAILS4", nullable = false, length = 40)
    private String paydetails4; //PaymentReferenceNo

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount = 0.0;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "APPROVEDBYSTAFFID", nullable = false, length = 40)
    private Integer approverid;

    @Column(name = "remarks", length = 40)
    private String remarks;

    @Column(name = "referenceno", nullable = false, length = 40)
    private String referenceno;

//    @DiffIgnore
    @Column(name = "xmldocument", nullable = false, length = 40)
    private String xmldocument;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "tdsflag", columnDefinition = "Boolean default false", nullable = false)
    private Boolean tdsflag;

    private Double tdsamount;

    @Column(name = "is_reversed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean is_reversed;

    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received;
    private LocalDate tds_received_date;

    private Integer tds_credit_doc_id;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buID;

    @Column(name = "lcoid", length = 40, updatable = false)
    private Integer lcoid;

    @Column(name = "invoiceid", nullable = false, length = 40)
    private Integer invoiceId;

    @Column(name = "paytype")
    private String paytype;

    @Column(name = "type", length = 25)
    private String type;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "receipt_number")
    private String reciptNo;

    @Column(name = "paymentreferenceno",  length = 40)
    private String paymentreferenceno;

//    @Transient
//    private Integer nextStaffId;

    //
    @ManyToMany(mappedBy = "creditDocumentList")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnoreProperties("creditDocumentList")
    @ToString.Exclude
    private List<DebitDocument> debitDocumentList;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;


    @Column(name = "bankid")
    private Long bankManagement;

    @Column(nullable = false, name = "destinationBank")
    private Long destinationBank;

    private String filename;
    private String uniquename;
    private Double barteramount;
    @Column(name = "tds_amount")
    private Double tdsAmount;
    @Column(name = "abbs_amount")
    private Double abbsAmount;

    @Column(name = "branch")
    private String branchname;

    @Column(name = "onlinesource")
    private String onlinesource;

    @Column(name = "creditdocumentno", nullable = false, length = 40)
    private String creditdocumentno;

    @Column(name = "ledger_id")
    private String ledgerId;

    @Transient
    double remainingAmount;

    @Transient
    String invoiceNumber;

    @Transient
    Integer loggedInUserId;



//    public CreditDocument(RecordPaymentPojo pojo) {
//        this.bankManagement = getBankManagement();
//    }
//
//
//    public CreditDocument(CreditDocMessage creditDocument, Customers customers) {
//        this.id = creditDocument.getId();
//        this.customer = customers;
//        this.paymode = creditDocument.getPaymode();
//        this.paymentdate = LocalDate.parse(creditDocument.getPaymentdate());
//        this.paydetails1 = creditDocument.getPaydetails1();
//        this.paydetails2 = creditDocument.getPaydetails2();
//        this.paydetails3 = creditDocument.getPaydetails3();
//        this.paydetails4 = creditDocument.getPaydetails4();
//        this.amount = creditDocument.getAmount();
//        this.status = creditDocument.getStatus();
//        this.remarks = creditDocument.getRemarks();
//        this.referenceno = creditDocument.getReferenceno();
//        this.isDelete = creditDocument.getIsDelete();
//        this.tdsflag = creditDocument.getTdsflag();
//        this.tdsamount = creditDocument.getTdsamount();
//        this.is_reversed = creditDocument.getIs_reversed();
//        this.resevrsed_date = creditDocument.getResevrsed_date();
//        this.resverse_debitdoc_id = creditDocument.getResverse_debitdoc_id();
//        this.tds_received = creditDocument.getTds_received();
//        this.tds_received_date = creditDocument.getTds_received_date();
//        this.tds_credit_doc_id = creditDocument.getTds_credit_doc_id();
//        this.mvnoId = creditDocument.getMvnoId();
//        this.buID = creditDocument.getBuID();
//        this.lcoid = creditDocument.getLcoid();
//        this.invoiceId = creditDocument.getInvoiceId();
//        this.paytype = creditDocument.getPaytype();
//        this.type = creditDocument.getType();
//        this.nextTeamHierarchyMappingId = creditDocument.getNextTeamHierarchyMappingId();
//        this.reciptNo = creditDocument.getReciptNo();
//        this.adjustedAmount = creditDocument.getAdjustedAmount();
//        this.bankManagement = creditDocument.getBankManagement();
//        this.destinationBank = creditDocument.getDestinationBank();
//        this.filename = creditDocument.getFilename();
//        this.uniquename = creditDocument.getUniquename();
//        this.barteramount = creditDocument.getBarteramount();
//        this.abbsAmount = creditDocument.getAbbsAmount();
//        this.branchname = creditDocument.getBranchname();
//        this.onlinesource = creditDocument.getOnlinesource();
//        this.creditdocumentno = creditDocument.getCreditdocumentno();
//        this.xmldocument=creditDocument.getXmldocument();
//        if (creditDocument.getChequedate()!=null) {
//            this.chequedate = LocalDate.parse(creditDocument.getChequedate());
//        }
   // }
}
