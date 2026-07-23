package com.savbill.revenuemanagement.core.entity.ladger;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "TBLTCREDITDOC",
        uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTID", "referenceno"})
)
@EntityListeners(AuditableListener.class)
public class CreditDocument extends Auditable {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CREDITDOCID", nullable = false, length = 40)
    private Integer id;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    @ToString.Exclude
    private Customers customer;

    @Column(name = "PAYMODE", nullable = false, length = 40)
    private String paymode;

    @DiffIgnore
    @Column(name = "paymentdate", nullable = false, length = 40)
    private LocalDate paymentdate;

    @Column(name = "chequedate", length = 40)
    private LocalDate chequedate;

    @DiffIgnore
    @Column(name = "PAYDETAILS1", nullable = false, length = 40)
    private String paydetails1; //Bank

    @DiffIgnore
    @Column(name = "PAYDETAILS2", nullable = false, length = 40)
    private String paydetails2; //Branch

    @Column(name = "PAYDETAILS3", nullable = false, length = 40)
    private String paydetails3; //ChequeNo

    @DiffIgnore
    @Column(name = "PAYDETAILS4", nullable = false, length = 40)
    private String paydetails4; //PaymentReferenceNo

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount = 0.0;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "APPROVEDBYSTAFFID", nullable = false, length = 40)
    private Integer approverid;

    @Column(name = "remarks", length = 40)
    private String remarks;

    @Column(name = "referenceno", nullable = false, length = 40)
    private String referenceno;

    @DiffIgnore
    @Column(name = "xmldocument", nullable = false, length = 40)
    private String xmldocument;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @DiffIgnore
    @Column(name = "tdsflag", columnDefinition = "Boolean default false", nullable = false)
    private Boolean tdsflag;

    @DiffIgnore
    private Double tdsamount;

    @DiffIgnore
    @Column(name = "is_reversed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean is_reversed;

    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received;
    private LocalDate tds_received_date;

    private Integer tds_credit_doc_id;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buID;

    @DiffIgnore
    @Column(name = "lcoid", length = 40, updatable = false)
    private Integer lcoid;

    @DiffIgnore
    @Column(name = "invoiceid", nullable = false, length = 40)
    private Integer invoiceId;

    @Column(name = "paytype")
    private String paytype;

    @DiffIgnore
    @Column(name = "type", length = 25)
    private String type;

    @DiffIgnore
    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @DiffIgnore
    @Column(name = "receipt_number")
    private String reciptNo;

    @DiffIgnore
    @Column(name = "paymentreferenceno",  length = 40)
    private String paymentreferenceno;

//    @Transient
//    private Integer nextStaffId;

    //
    @DiffIgnore
    @ManyToMany(mappedBy = "creditDocumentList")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnoreProperties("creditDocumentList")
    @ToString.Exclude
    private List<DebitDocument> debitDocumentList;

    @DiffIgnore
    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;

    @DiffIgnore
    @Column(name = "bankid")
    private Long bankManagement;

    @Column(nullable = false, name = "destinationBank")
    private Long destinationBank;

    private String filename;
    private String uniquename;
    private Double barteramount;
    @DiffIgnore
    @Column(name = "tds_amount")
    private Double tdsAmount;
    @DiffIgnore
    @Column(name = "abbs_amount")
    private Double abbsAmount;

    @Column(name = "branch")
    private String branchname;

    @Column(name = "onlinesource")
    private String onlinesource;

    @Column(name = "creditdocumentno", nullable = false, length = 40)
    private String creditdocumentno;
    @Column(name= "qr_code")
    private String qrCode;
    @Column(name = "is_kra_synced", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private Boolean isKraSynced = false;

    @DiffIgnore
    @Column(name = "ledger_id")
    private String ledgerId;

    @Transient
    double remainingAmount;

    @Transient
    String invoiceNumber;

    @Transient
    Integer loggedInUserId;

    @DiffIgnore
    @Column(name = "batchassigned")
    private Boolean batchAssigned;

    @DiffIgnore
    @Column(name = "withdrawcreditdocid")
    private Integer withDrawCreditdocId;

    @DiffIgnore
    @Column(name = "trialdebitdocid" , length = 40)
    private Integer trialDebitdocId;

    @DiffIgnore
    @Column(name = "from_id" , length = 40)
    private Integer fromId;

    @DiffIgnore
    @Column(name = "to_id" , length = 40)
    private Integer toId;

    @Transient
    private String custName;





    public CreditDocument(RecordPaymentPojo pojo) {
        this.bankManagement = getBankManagement();
    }


    public CreditDocument(CreditDocMessage creditDocument, Customers customers) {
        this.id = creditDocument.getId();
        this.customer = customers;
        this.paymode = creditDocument.getPaymode();
        this.paymentdate = LocalDate.parse(creditDocument.getPaymentdate());
        this.paydetails1 = creditDocument.getPaydetails1();
        this.paydetails2 = creditDocument.getPaydetails2();
        this.paydetails3 = creditDocument.getPaydetails3();
        this.paydetails4 = creditDocument.getPaydetails4();
        this.amount = creditDocument.getAmount();
        this.status = "pending";
        this.remarks = creditDocument.getRemarks();
        this.referenceno = creditDocument.getReferenceno();
        this.isDelete = creditDocument.getIsDelete();
        this.tdsflag = creditDocument.getTdsflag();
        this.tdsamount = creditDocument.getTdsamount();
        this.is_reversed = creditDocument.getIs_reversed();
        this.resevrsed_date = creditDocument.getResevrsed_date();
        this.resverse_debitdoc_id = creditDocument.getResverse_debitdoc_id();
        this.tds_received = creditDocument.getTds_received();
        this.tds_received_date = creditDocument.getTds_received_date();
        this.tds_credit_doc_id = creditDocument.getTds_credit_doc_id();
        this.mvnoId = creditDocument.getMvnoId();
        this.buID = creditDocument.getBuID();
        this.lcoid = creditDocument.getLcoid();
        this.invoiceId = creditDocument.getInvoiceId();
        this.paytype = creditDocument.getPaytype();
        this.type = creditDocument.getType();
        this.nextTeamHierarchyMappingId = creditDocument.getNextTeamHierarchyMappingId();
        this.reciptNo = creditDocument.getReciptNo();
        this.adjustedAmount = creditDocument.getAdjustedAmount();
        this.bankManagement = creditDocument.getBankManagement();
        this.destinationBank = creditDocument.getDestinationBank();
        this.filename = creditDocument.getFilename();
        this.uniquename = creditDocument.getUniquename();
        this.barteramount = creditDocument.getBarteramount();
        this.abbsAmount = creditDocument.getAbbsAmount();
        this.branchname = creditDocument.getBranchname();
        this.onlinesource = creditDocument.getOnlinesource();
        this.creditdocumentno = creditDocument.getCreditdocumentno();
        this.xmldocument=creditDocument.getXmldocument();
        this.paymentreferenceno=creditDocument.getPaymentreferenceno();
        if (creditDocument.getChequedate()!=null) {
            this.chequedate = LocalDate.parse(creditDocument.getChequedate());
        }
    }

    public CreditDocument(Double amount,double adjustedAmount,String paymode,int id,double remainingAmount,String referenceno,String creditdocumentno,Integer invoiceId){
        this.amount=amount;
        this.adjustedAmount=adjustedAmount;
        this.paymode=paymode;
        this.id=id;
        this.remainingAmount=remainingAmount;
        this.referenceno=referenceno;
        this.creditdocumentno=creditdocumentno;
        this.invoiceId=invoiceId;
    }

    public CreditDocument(Double amount,double adjustedAmount,String paymode,int id,double remainingAmount,String referenceno,String creditdocumentno,Integer invoiceId,String paymentreferenceno,Integer withDrawCreditdocId){
        this.amount=amount;
        this.adjustedAmount=adjustedAmount;
        this.paymode=paymode;
        this.id=id;
        this.remainingAmount=remainingAmount;
        this.referenceno=referenceno;
        this.creditdocumentno=creditdocumentno;
        this.invoiceId=invoiceId;
        this.paymentreferenceno=paymentreferenceno;
        this.withDrawCreditdocId=withDrawCreditdocId;
    }

    public CreditDocument(Double amount,double adjustedAmount,String paymode,int id,double remainingAmount,String referenceno,String creditdocumentno,Integer invoiceId,String paymentreferenceno){
        this.amount=amount;
        this.adjustedAmount=adjustedAmount;
        this.paymode=paymode;
        this.id=id;
        this.remainingAmount=remainingAmount;
        this.referenceno=referenceno;
        this.creditdocumentno=creditdocumentno;
        this.invoiceId=invoiceId;
        this.paymentreferenceno=paymentreferenceno;
    }

    public CreditDocument(Integer id , String refNo , String receiptNo , String remarks , String paytype,String status){
        this.id = id;
        this.referenceno = refNo;
        this.reciptNo = receiptNo;
        this.remarks = remarks;
        this.paytype = paytype;
        this.status=status;
    }

    public CreditDocument(Integer id,String paymode , LocalDate paymentdate , String payDetails1 , String payDetails2 , String payDetails3 , String payDetails4 ,Double amount,String status,Integer createdById, String createdByName,String remarks, String receiptNo,Customers customers,String type,Integer nextApprover,String filename,Integer nextTeamHierarchyMappingId,String creditdocumentno,String refNo,Double tdsamount,Double abbsAmount, Double adjustedAmount,Long bankManagement,String onlinesource,Integer mvnoId,Integer invoiceId){
        this.id = id;
        this.paymode = paymode;
        this.paymentdate = paymentdate;
        this.paydetails1 = payDetails1;
        this.paydetails2 = payDetails2;
        this.paydetails3 = payDetails3;
        this.paydetails4 = payDetails4;
        this.amount = amount;
        this.status = status;
        this.setCreatedById(createdById);
        this.setCreatedByName(createdByName);
        this.remarks = remarks;
        this.reciptNo = receiptNo;
        this.customer = customers;
        this.type = type;
        this.approverid = nextApprover;
        this.filename = filename;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.creditdocumentno = creditdocumentno;
        this.referenceno = refNo;
        this.tdsamount = tdsamount;
        this.abbsAmount = abbsAmount;
        this.adjustedAmount = adjustedAmount;
        this.bankManagement=bankManagement;
        this.onlinesource=onlinesource;
        this.mvnoId=mvnoId;
        this.invoiceId=invoiceId;

    }

    public CreditDocument(Integer id,String paymode , LocalDate paymentdate , String payDetails1 , String payDetails2 , String payDetails3 , String payDetails4 ,Double amount,String status,Integer createdById, String createdByName,String remarks, String receiptNo,Customers customers,String type,Integer nextApprover,String filename,Integer nextTeamHierarchyMappingId,String creditdocumentno,String refNo,Double tdsamount,Double abbsAmount, Double adjustedAmount,Long bankManagement,String onlinesource,Integer mvnoId,Integer invoiceId,String paytype, String paymentreferenceno,String custName, LocalDateTime createdate){
        this.id = id;
        this.paymode = paymode;
        this.paymentdate = paymentdate;
        this.paydetails1 = payDetails1;
        this.paydetails2 = payDetails2;
        this.paydetails3 = payDetails3;
        this.paydetails4 = payDetails4;
        this.amount = amount;
        this.status = status;
        this.setCreatedById(createdById);
        this.setCreatedByName(createdByName);
        this.remarks = remarks;
        this.reciptNo = receiptNo;
        this.customer = customers;
        this.type = type;
        this.approverid = nextApprover;
        this.filename = filename;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.creditdocumentno = creditdocumentno;
        this.referenceno = refNo;
        this.tdsamount = tdsamount;
        this.abbsAmount = abbsAmount;
        this.adjustedAmount = adjustedAmount;
        this.bankManagement=bankManagement;
        this.onlinesource=onlinesource;
        this.mvnoId=mvnoId;
        this.invoiceId=invoiceId;
        this.paytype=paytype;
        this.paymentreferenceno=paymentreferenceno;
        this.custName=custName;
        this.setCreatedate(createdate);
    }


}
