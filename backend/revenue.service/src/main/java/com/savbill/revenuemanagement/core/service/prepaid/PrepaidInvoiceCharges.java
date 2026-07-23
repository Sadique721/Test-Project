package com.savbill.revenuemanagement.core.service.prepaid;

import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Data
public class PrepaidInvoiceCharges {

//    private List<ItemCharge> itemCharges;

    private Integer custId;

    private String customerName;

    private String customerType;

    private Double totalDirectChargeAmount;

    private Long invoiceId;

    private String customerUsername;

    private boolean isOrgCust = false;

    private Double totalInvoiceAmount;

    Integer loggedInUserId;
    HashSet<Integer> oldDebitDocumentId;

    List<Long> custServiceIds;

    private String creditDocumentId = "creditDocumentId";

    private String isFromFlutterWave = "isFromFlutterWave";

    private String isCaf;

    private String paymentStatus;

    private Integer billRunId;

    private String createdByName;

    public PrepaidInvoiceCharges() {
    }

    private Long inventoryMappingId;

    private DebitDocument debitDocument;
    private Double walletBalance;

    List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPair ;

    List<Map.Entry<Integer, String>> CustPackAndEndDatePair;

    Double adjustedAmount;

    String billRunStatus;

    private Boolean isVoid;

    Boolean isPaymentApproved;
    private Boolean  isDirectChargeInvoice;
    private List<Integer> chargeIds;

    private  String nextBilldate;

    List<Map.Entry<Integer, String>> childIdNextBillDatePair;

    public PrepaidInvoiceCharges(Integer custId, String customerName, String customerType, Double totalDirectChargeAmount, Long invoiceId, String customerUsername, boolean isOrgCust, Double totalInvoiceAmount, Integer loggedInUserId, HashSet<Integer> oldDebitDocumentId, List<Long> custServiceIds, String creditDocumentId, String isFromFlutterWave, String isCaf, Long inventoryMappingId,DebitDocument debitDocument,Double walletbalance,String paymentStatus,Integer billRunId,String createByName,List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPairs,Double adjustedAmount,String billRunStatus,Boolean isPaymentApproved, Boolean isDirectChargeInvoice, Boolean isVoid,String nextBilldate,List<Map.Entry<Integer, String>> custPackAndEndDatePair, List<Map.Entry<Integer, String>> childIdNextBillDatePair) {
        this.custId = custId;
        this.customerName = customerName;
        this.customerType = customerType;
        this.totalDirectChargeAmount = totalDirectChargeAmount;
        this.invoiceId = invoiceId;
        this.customerUsername = customerUsername;
        this.isOrgCust = isOrgCust;
        this.totalInvoiceAmount = totalInvoiceAmount;
        this.loggedInUserId = loggedInUserId;
        this.oldDebitDocumentId = oldDebitDocumentId;
        this.custServiceIds = custServiceIds;
        this.creditDocumentId = creditDocumentId;
        this.isFromFlutterWave = isFromFlutterWave;
        this.isCaf = isCaf;
        this.inventoryMappingId = inventoryMappingId;
        this.debitDocument=this.getdebitDocument(debitDocument);
        this.walletBalance=walletbalance;
        this.billRunId=billRunId;
        this.createdByName=createByName;
        this.paymentStatus=paymentStatus;
        this.CustPackAndDebitDocIdPair=CustPackAndDebitDocIdPairs;
        this.adjustedAmount=adjustedAmount;
        this.billRunStatus=billRunStatus;
        this.isPaymentApproved=isPaymentApproved;
        this.isDirectChargeInvoice=isDirectChargeInvoice;
        this.isVoid=isVoid;
        this.nextBilldate = nextBilldate;
        this.CustPackAndEndDatePair = custPackAndEndDatePair;
        this.childIdNextBillDatePair = childIdNextBillDatePair;
    }

    public DebitDocument getdebitDocument(DebitDocument obj){
        DebitDocument debitDocument=new DebitDocument();
        debitDocument.setId(obj.getId());
        debitDocument.setDocument(obj.getDocument());
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        debitDocument.setLocalenddate( obj.getBilldate().format(formatter).toString());
        debitDocument.setLocalstartdate(obj.getStartdate().format(formatter).toString());
        debitDocument.setLocalenddate(obj.getEndate().format(formatter).toString());
        debitDocument.setDuedateString(obj.getDuedate().format(formatter).toString());
        debitDocument.setLatepaymentdateString(obj.getLatepaymentdate().format(formatter).toString());
        debitDocument.setSubtotal(obj.getSubtotal());
        debitDocument.setTax(obj.getTax());
        debitDocument.setDiscount(obj.getDiscount());
        debitDocument.setTotalamount(obj.getTotalamount());
        debitDocument.setPreviousbalance(obj.getPreviousbalance());
        debitDocument.setLatepaymentfee(obj.getLatepaymentfee());
        debitDocument.setCurrentpayment(obj.getCurrentpayment());
        debitDocument.setCurrentdebit(obj.getCurrentdebit());
        debitDocument.setCurrentcredit(obj.getCurrentcredit());
        debitDocument.setTotaldue(obj.getTotaldue());
 //       debitDocument.setTotalamountinwords(obj.getAmountinwords());
       // debitDocument.dueinwords=obj.getDueinwords();
        debitDocument.setBillrunid(obj.getBillrunid());
        debitDocument.setBillrunstatus(obj.getBillrunstatus());
        debitDocument.setStatus(obj.getStatus());
        debitDocument.setIsDelete(obj.getIsDelete());
        debitDocument.setCstchargeid(obj.getCstchargeid());
        debitDocument.setPaymentowner(obj.getPaymentowner());
        debitDocument.setDebitDocumentTAXRels(obj.getDebitDocumentTAXRels());
        debitDocument.setDebitDocDetailsList(obj.getDebitDocDetailsList());
        debitDocument.setDocnumber(obj.getDocnumber());
        debitDocument.setTotalCustomerDiscount(obj.getCustomer().getId().doubleValue());
        debitDocument.setDocnumber(obj.getDocnumber());
        debitDocument.setCustRefName(obj.getCustRefName());
        debitDocument.setStaffid(getLoggedInUserId());
        debitDocument.setCustpackrelid(obj.getCustpackrelid());
        debitDocument.setNextStaff(obj.getNextStaff());
        debitDocument.setRemarks(obj.getRemarks());
        debitDocument.setUpdateDebitDpcDetailsIds(obj.getUpdateDebitDpcDetailsIds());
      //  debitDocument.inventoryMappingId=obj.getInventoryMappingId();
        return debitDocument;
    }
}
