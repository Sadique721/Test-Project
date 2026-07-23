package com.savbill.revenuemanagement.rabbitmq.messages;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocumentDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostPaidTrailInvoiceMessage {
    private Integer id;
    private String docnumber;
    private Long customerId;
    private String billdate;
    private String createdate;
    private String startdate;
    private String endate;
    private String duedate;
    private String latepaymentdate;
    private double subtotal;
    private double tax;
    private double discount;
    private double totalamount;
    private double previousbalance;
    private double latepaymentfee;
    private double currentpayment;
    private double currentdebit;
    private double currentcredit;
    private double totaldue;
    private String amountinwords;
    private String dueinwords;
    private Integer billrunid;
    private String billrunstatus;
    private String document;
    private Boolean isDelete;
    private String createdByName;
    private String lastModifiedByName;
    private Integer custpackrelid;
    private String billableToName;

    private List<TrialDebitDocumentDetail> trialDebitDocumentDetails;

    public PostPaidTrailInvoiceMessage(Integer id,String docnumber, Long customerId, String billdate, String createdate, String startdate, String endate, String duedate,String latepaymentdate, double subtotal,double tax, double discount, double totalamount, double previousbalance, double latepaymentfee, double currentpayment, double currentdebit, double currentcredit, double totaldue, String amountinwords, String dueinwords, Integer billrunid, String billrunstatus, String document, Boolean isDelete, String createdByName, String lastModifiedByName, Integer custpackrelid, String billableToName, List<TrialDebitDocumentDetail> trialDebitDocumentDetails)
    {
        this.id = id;
        this.docnumber = docnumber;
        this.customerId = customerId;
        this.billdate = billdate;
        this.createdate = createdate;
        this.startdate = startdate;
        this.endate = endate;
        this.duedate = duedate;
        this.latepaymentdate = latepaymentdate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.totalamount = totalamount;
        this.previousbalance = previousbalance;
        this.latepaymentfee = latepaymentfee;
        this.currentpayment = currentpayment;
        this.currentdebit = currentdebit;
        this.currentcredit = currentcredit;
        this.totaldue = totaldue;
        this.amountinwords = amountinwords;
        this.dueinwords = dueinwords;
        this.billrunid = billrunid;
        this.billrunstatus = billrunstatus;
        this.document = document;
        this.isDelete = isDelete;
        this.createdByName = createdByName;
        this.lastModifiedByName = lastModifiedByName;
        this.custpackrelid = custpackrelid;
        this.billableToName = billableToName;
        this.trialDebitDocumentDetails = trialDebitDocumentDetails;
    }

}


