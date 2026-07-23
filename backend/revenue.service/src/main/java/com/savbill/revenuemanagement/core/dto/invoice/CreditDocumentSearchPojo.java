package com.savbill.revenuemanagement.core.dto.invoice;

import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor

public class CreditDocumentSearchPojo {

    private Integer id;
    private String customerName;
    private Double amount;
    private String documentno;
    private String invoiceNumber;
    private String paydetails2;
    private Double tdsamount;
    private Double abbsAmount;
    private String referenceno;
    private String paymode;
    private String type;
    private LocalDate paymentdate;
    private String status;
    private Integer approverid;
    private Integer nextTeamHierarchyMappingId;

    private String createbyname;

    private String remarks;

    private Integer custId;

    private String acctno;

    public CreditDocumentSearchPojo(Integer id, String customerName, Double amount, String documentno, String invoiceNumber, String paydetails2, Double tdsamount, Double abbsAmount, String referenceno, String paymode, String type, LocalDate paymentdate, String status, Integer approverid, Integer nextTeamHierarchyMappingId, String createbyname, String remarks, Integer custId) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.documentno = documentno;
        this.invoiceNumber = invoiceNumber;
        this.paydetails2 = paydetails2;
        this.tdsamount = tdsamount;
        this.abbsAmount = abbsAmount;
        this.referenceno = referenceno;
        this.paymode = paymode;
        this.type = type;
        this.paymentdate = paymentdate;
        this.status = status;
        this.approverid = approverid;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.createbyname = createbyname;
        this.remarks = remarks;
        this.custId = custId;
    }

    public CreditDocumentSearchPojo(CreditDocument debitDocument) {
        this.id = debitDocument.getId();
        this.customerName = debitDocument.getCustomer().getFirstname();
        this.amount = debitDocument.getAmount();
        this.documentno = debitDocument.getCreditdocumentno();
        this.invoiceNumber = debitDocument.getInvoiceNumber();
        this.paydetails2 = debitDocument.getPaydetails2();
        this.tdsamount = debitDocument.getTdsamount();
        this.abbsAmount = debitDocument.getAbbsAmount();
        this.referenceno = debitDocument.getReferenceno();
        this.paymode = debitDocument.getPaymode();
        this.type = debitDocument.getType();
        this.paymentdate = debitDocument.getPaymentdate();
        this.status = debitDocument.getStatus();
        this.approverid = debitDocument.getApproverid();
        this.nextTeamHierarchyMappingId = debitDocument.getNextTeamHierarchyMappingId();
        this.createbyname = debitDocument.getCreatedByName();
        this.remarks = debitDocument.getRemarks();
        this.custId = debitDocument.getCustomer().getId();
        this.acctno = debitDocument.getCustomer().getAcctno();
    }
}
