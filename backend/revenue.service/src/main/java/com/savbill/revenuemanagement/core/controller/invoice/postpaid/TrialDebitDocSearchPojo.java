package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TrialDebitDocSearchPojo {

    private Integer custid;
    private String customerName;
    private String paymentStatus;
    private Double adjustedAmount;
    private Integer nextStaff;
    private Integer nextTeamHierarchyMappingId;
    private String billrunstatus;
    private LocalDateTime createdate;
    private Double totalamount;
    private String docnumber;
    private LocalDateTime billdate;

    private String createdByName;

    private String custType;

    private String billableToName;

    private Integer billrunid;

    private String amountinwords;

    private Double discount;

    private LocalDateTime latepaymentdate;

    private LocalDateTime startdate;

    private LocalDateTime endate;

    private LocalDateTime duedate;

    private Integer id;

    private Double tax;

    private String status;

    private String custRefName;


    public TrialDebitDocSearchPojo(TrialDebitDocument debitDocument) {
        this.custid = debitDocument.getCustomer().getId();
        this.customerName = debitDocument.getCustomer().getCustname();
        this.billrunstatus = debitDocument.getBillrunstatus();
        this.createdate = debitDocument.getCreatedate();
        this.totalamount = debitDocument.getTotalamount();
        this.docnumber = debitDocument.getDocnumber();
        this.billdate = debitDocument.getBilldate();
        this.createdByName = debitDocument.getCreatedByName();
        this.custType = debitDocument.getCustomer().getCusttype();
        this.billableToName = debitDocument.getBillableToName();
        this.billrunid = debitDocument.getBillrunid();
        this.amountinwords = debitDocument.getAmountinwords();
        this.discount = debitDocument.getDiscount();
        this.latepaymentdate = debitDocument.getLatepaymentdate();
        this.startdate = debitDocument.getStartdate();
        this.endate = debitDocument.getEndate();
        this.id = debitDocument.getId();
        this.tax = debitDocument.getTax();
        this.status = debitDocument.getBillrunstatus();
        this.duedate = debitDocument.getDuedate();
    }


}
