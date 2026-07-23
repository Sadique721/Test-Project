package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCREDITDOC")
public class CreditDocumentData {


    @Id
    @Column(name = "CREDITDOCID", nullable = false, length = 40)
    private Integer id;


    @JoinColumn(name = "CUSTID")
    private Integer customer;

    @Column(name = "PAYMODE", nullable = false, length = 40)
    private String paymode;

//    @Column(name = "paymentdate", nullable = false, length = 40)
//    private LocalDate paymentdate;

    @Column(name = "chequedate", length = 40)
    private String chequedate;

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

    @Column(name = "xmldocument", nullable = false, length = 40)
    private String xmldocument;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "tdsflag", columnDefinition = "Boolean default false", nullable = false)
    private Boolean tdsflag;

//    private Double tdsamount;

    @Column(name = "is_reversed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isReversed;

    @Column(name = "resevrsed_date")
    private LocalDate resevrsedDate;

    @Column(name = "resverse_debitdoc_id")
    private Integer resverseDebitdocId;

    @Column(name = "tds_received")
    private Boolean tdsReceived;

    @Column(name = "tds_received_date")
    private LocalDate tdsReceivedDate;

    @Column(name = "tds_credit_doc_id")
    private Integer tdsCreditDocId;

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
    Double tdsAmount;
    @Column(name = "abbs_amount")
    Double abbsAmount;

    @Column(name = "branch")
    private String branchname;

    @Column(name = "onlinesource")
    private String onlinesource;

    @Column(name = "creditdocumentno", nullable = false, length = 40)
    private String creditdocumentno;

    @Column(name = "paymentdate", length = 40)
    private String paymentdate;

    @Transient
    private List<CreditDebitDocMapping> creditDebitDocMappingList;

    private String ledgerId;

    private Double remainingAmount;

    @Column(name = "CREATEDATE")
    private String createdate;
    @Column(name = "CREATEDBYSTAFFID")
    private Integer createdbystaffid;
    @Column(name = "LASTMODIFIEDBYSTAFFID")
    private Integer lastmodifiedbystaffid;
    @Column(name = "LASTMODIFIEDDATE")
    private String lastmodifieddate;
    @Column(name = "ird_sync")
    private String irdSync;

    @Column(name = "ird_resp_code")
    private String irdRespCode;

    public CreditDocumentData(CreditDocMessage creditDocument) {
        this.id = creditDocument.getId();
        this.customer = creditDocument.getCustomer();
        this.paymode = creditDocument.getPaymode();
        this.paymentdate = creditDocument.getPaymentdate();
        this.paydetails1 = creditDocument.getPaydetails1();
        this.paydetails2 = creditDocument.getPaydetails2();
        this.paydetails3 = creditDocument.getPaydetails3();
        this.paydetails4 = creditDocument.getPaydetails4();
        this.amount = creditDocument.getAmount();
        this.status = creditDocument.getStatus();
        this.approverid = creditDocument.getApproverid();
        this.remarks = creditDocument.getRemarks();
        this.referenceno = creditDocument.getReferenceno();
        this.isDelete = creditDocument.getIsDelete();
        this.tdsflag = creditDocument.getTdsflag();
        this.tdsAmount = creditDocument.getTdsamount();
        this.isReversed = creditDocument.getIs_reversed();
        this.resevrsedDate = creditDocument.getResevrsed_date();
        this.resverseDebitdocId = creditDocument.getResverse_debitdoc_id();
        this.tdsReceived = creditDocument.getTds_received();
        this.tdsReceivedDate = creditDocument.getTds_received_date();
        this.tdsCreditDocId = creditDocument.getTds_credit_doc_id();
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
        this.xmldocument = creditDocument.getXmldocument();
        this.chequedate = creditDocument.getChequedate();
        this.ledgerId = creditDocument.getLedgerId();
        this.createdate = creditDocument.getCreatedate();
        this.createdbystaffid = creditDocument.getCreatedbystaffid();
        this.lastmodifiedbystaffid = creditDocument.getLastmodifiedbystaffid();
        this.lastmodifieddate = creditDocument.getLastmodifieddate();
        this.irdRespCode = creditDocument.getIrdRespCode();
        this.irdSync = creditDocument.getIrdSync();
    }
}
