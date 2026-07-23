package com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail;


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

    private List<Double> taxAmounts;


}
