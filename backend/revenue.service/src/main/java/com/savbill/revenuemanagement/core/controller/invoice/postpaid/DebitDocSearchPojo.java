package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DebitDocSearchPojo {

    private String remarks;
    private String lastModifiedByName;
    private String operationType;
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

    private Integer id;

    private Double tax;

    private String status;

    private String custRefName;
    private List<Integer> creditDocId;

    private  Boolean ispromiseToPayInOldCPR;

    private long  promiseToPayHoldDays;

    private LocalDate promiseStartDate;

    private LocalDate promiseEndDate;

    private String mvnoName;

    private String referenceNo;

    private LocalDateTime duedate;




    //for invoiceSearch in all screens
    public DebitDocSearchPojo(Integer custid, String customerName, String paymentStatus, Double adjustedAmount, Integer nextStaff, Integer nextTeamHierarchyMappingId, String billrunstatus, LocalDateTime createdate, Double totalamount, String docnumber, LocalDateTime billdate, String createdByName, String custType, String billableToName, Integer id, String status, String custRefName) {
        this.custid = custid;
        this.customerName = customerName;
        this.paymentStatus = paymentStatus;
        this.adjustedAmount = adjustedAmount;
        this.nextStaff = nextStaff;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.billrunstatus = billrunstatus;
        this.createdate = createdate;
        this.totalamount = totalamount;
        this.docnumber = docnumber;
        this.billdate = billdate;
        this.createdByName = createdByName;
        this.custType = custType;
        this.billableToName = billableToName;
        this.id = id;
        this.status = status;
        this.custRefName = custRefName;
    }

    public DebitDocSearchPojo(String customerName, String billrunstatus, LocalDateTime createdate, Double totalamount, String docnumber, LocalDateTime billdate, Integer billrunid, String amountinwords, Double discount, LocalDateTime latepaymentdate, LocalDateTime startdate, LocalDateTime endate, Double tax) {
        this.customerName = customerName;
        this.billrunstatus = billrunstatus;
        this.createdate = createdate;
        this.totalamount = totalamount;
        this.docnumber = docnumber;
        this.billdate = billdate;
        this.billrunid = billrunid;
        this.amountinwords = amountinwords;
        this.discount = discount;
        this.latepaymentdate = latepaymentdate;
        this.startdate = startdate;
        this.endate = endate;
        this.tax = tax;
    }

    public DebitDocSearchPojo(DebitDocument debitDocument) {
        this.custid = debitDocument.getCustomer().getId();
        this.customerName = debitDocument.getCustomer().getCustname();
        this.paymentStatus = debitDocument.getPaymentStatus();
        this.adjustedAmount = new BigDecimal(debitDocument.getAdjustedAmount()).setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.nextStaff = debitDocument.getNextStaff();
        this.nextTeamHierarchyMappingId = debitDocument.getNextTeamHierarchyMappingId();
        this.billrunstatus = debitDocument.getBillrunstatus();
        this.createdate = debitDocument.getCreatedate();
        this.totalamount = debitDocument.getTotalamount();
        this.docnumber = debitDocument.getDocnumber();
        this.billdate = debitDocument.getBilldate();
        this.createdByName = debitDocument.getCreatedByName();
        this.custType = debitDocument.getCustomer().getCusttype();
        this.billableToName = debitDocument.getBillableToName();
        this.billrunid = debitDocument.getBillrunid();
        this.amountinwords = debitDocument.getTotalamountinwords();
        this.discount = debitDocument.getDiscount();
        this.latepaymentdate = debitDocument.getLatepaymentdate();
        this.startdate = debitDocument.getStartdate();
        this.endate = debitDocument.getEndate();
        this.id = debitDocument.getId();
        this.tax = debitDocument.getTax();
        this.status = debitDocument.getStatus();
        this.custRefName = debitDocument.getCustRefName();
        this.operationType = debitDocument.getOperationType();
        this.lastModifiedByName = debitDocument.getLastModifiedByName();
        this.remarks = debitDocument.getRemarks();
        this.creditDocId=debitDocument.getCreditDocumentList().stream().map(i->i.getId()).collect(Collectors.toList());
        this.mvnoName = debitDocument.getMvnoName();
        this.duedate = debitDocument.getDuedate();

    }

    public DebitDocSearchPojo(DebitDocument debitDocument, List<CustPlanMappping> custPlanMappping) {
        this.custid = debitDocument.getCustomer().getId();
        this.customerName = debitDocument.getCustomer().getCustname();
        this.paymentStatus = debitDocument.getPaymentStatus();
        this.adjustedAmount = new BigDecimal(debitDocument.getAdjustedAmount()).setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.nextStaff = debitDocument.getNextStaff();
        this.nextTeamHierarchyMappingId = debitDocument.getNextTeamHierarchyMappingId();
        this.billrunstatus = debitDocument.getBillrunstatus();
        this.createdate = debitDocument.getCreatedate();
        this.totalamount = debitDocument.getTotalamount();
        this.docnumber = debitDocument.getDocnumber();
        this.billdate = debitDocument.getBilldate();
        this.createdByName = debitDocument.getCreatedByName();
        this.custType = debitDocument.getCustomer().getCusttype();
        this.billableToName = debitDocument.getBillableToName();
        this.billrunid = debitDocument.getBillrunid();
        this.amountinwords = debitDocument.getTotalamountinwords();
        if (debitDocument.getDiscount()>=0) {
            this.discount = debitDocument.getDiscount();
        }else {
            this.discount = 0d;
        }
        this.duedate = debitDocument.getDuedate();
        this.latepaymentdate = debitDocument.getLatepaymentdate();
        this.startdate = debitDocument.getStartdate();
        this.endate = debitDocument.getEndate();
        this.id = debitDocument.getId();
        this.tax = debitDocument.getTax();
        this.status = debitDocument.getStatus();
        this.custRefName = debitDocument.getCustRefName();
        this.operationType = debitDocument.getOperationType();
        this.lastModifiedByName = debitDocument.getLastModifiedByName();
        this.remarks = debitDocument.getRemarks();
        this.creditDocId=debitDocument.getCreditDocumentList().stream().map(i->i.getId()).collect(Collectors.toList());
        if (!custPlanMappping.isEmpty() && custPlanMappping.get(0).getPromisetopay_renew_count()!=null){
            this.promiseStartDate = LocalDate.from(custPlanMappping.get(0).getPromise_to_pay_startdate());
            this.promiseEndDate = LocalDate.from(custPlanMappping.get(0).getPromise_to_pay_enddate());
            this.promiseToPayHoldDays = ChronoUnit.DAYS.between(promiseStartDate, promiseEndDate);
            this.ispromiseToPayInOldCPR = true;
        }

    }
}
