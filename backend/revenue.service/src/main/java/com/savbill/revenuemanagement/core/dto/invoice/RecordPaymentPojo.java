package com.savbill.revenuemanagement.core.dto.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordPaymentPojo {

    private String referenceno;
    //@JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chequedate;

    private String chequedatestr;
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentdate = LocalDate.now();

    private String paymentdatestr;

    private String chequeno;

    private String bank;

    private Integer customerid;

    private Integer toCustomerId;

    private String paymode;

    private Double amount;

    private String paymentreferenceno;

    private String remark;

    private String branch;

    private List<Integer> invoiceId; //add list of invoiceid here

    private String type;

    private String paytype;

    private Integer mvnoId;

    private Long buId;
    private String bankManagement;
    private Long destinationBank;

    private Integer nextApprover;

    private Integer nextStaffId;
    private String reciptNo;
    private String filename;
    private String uniquename;
    private Double barteramount;
    private Double tdsAmount;
    private Double abbsAmount;
    private Integer creditDocId;
    private String onlinesource;
    private Long customerMappingId;
    private Boolean isAdjusted;
    private List<Integer> withDrawCreditdocId;
    private List<PaymentListPojo> paymentListPojos;

    public List<PaymentListPojo> getPaymentListPojos() {
        return paymentListPojos;
    }

    public void setPaymentListPojos(List<PaymentListPojo> paymentListPojos) {
        this.paymentListPojos = paymentListPojos;
    }

    public String getOnlinesource() {
        return onlinesource;
    }

    public void setOnlinesource(String onlinesource) {
        this.onlinesource = onlinesource;
    }



    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    private String file;
    public String getBatchname() {
        return batchname;
    }
    public void setBatchname(String batchname) {
        this.batchname = batchname;
    }
    private String batchname;



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public Double getBarteramount() {
        return barteramount;
    }

    public void setBarteramount(Double barteramount) {
        this.barteramount = barteramount;
    }

    public String getBankManagement() {
        return bankManagement;
    }

    public void setBankManagement(String bankManagement) {
        this.bankManagement = bankManagement;
    }

    public Long getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(Long destinationBank) {
        this.destinationBank = destinationBank;
    }

    public String getRemark() {
        return remark;
    }

}
