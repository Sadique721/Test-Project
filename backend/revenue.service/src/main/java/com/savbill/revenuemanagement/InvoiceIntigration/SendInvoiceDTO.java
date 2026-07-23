package com.savbill.revenuemanagement.InvoiceIntigration;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SendInvoiceDTO {

    private Integer debitdocId;

    private String debitDocNumber;

    private LocalDateTime debitDocDate;

    private Double basePrice;
    private Double tempTaxAmount;
    private Double totalAmount;

    private List<Double> taxAmounts = new ArrayList<>();

    private Integer mvnoId;

    private String clientId;

    private String customerTin;

    private String customerDrivingLicence;

    private String customerVrn;

    private String customerNid;

    private String customerPassport;

    private String firstname;

    private String lastname;

    private String phoneNumber;

    public void addTaxAmount(Double taxAmount){
        taxAmounts.add(taxAmount);
    }

    public SendInvoiceDTO(Integer debitdocId, String debitDocNumber, LocalDateTime debitDocDate, Double basePrice, Double tempTaxAmount, Double totalAmount,String clientId,Integer mvnoId, String customerNid , String customerVrn , String customerTin , String customerPassport  , String customerDrivingLicence , String firstname , String lastname, String phoneNumber) {
        this.debitdocId = debitdocId;
        this.debitDocNumber = debitDocNumber;
        this.debitDocDate = debitDocDate;
        this.basePrice = basePrice;
        this.tempTaxAmount = tempTaxAmount;
        this.totalAmount = totalAmount;
        this.clientId = clientId;
        this.mvnoId = mvnoId;
        this.customerNid = customerNid;
        this.customerVrn  = customerVrn;
        this.customerTin  = customerTin;
        this.customerPassport =  customerPassport;
        this.customerDrivingLicence = customerDrivingLicence;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;

    }

}
