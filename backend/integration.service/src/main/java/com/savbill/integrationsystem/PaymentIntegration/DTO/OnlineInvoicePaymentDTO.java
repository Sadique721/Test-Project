package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OnlineInvoicePaymentDTO {

    private Integer invoiceId;

    private Integer custId;

    private Double amount;

    private Integer mvnoId;

    private List<Long> buId;

    private String paymentGatewayName;

    private Integer createdById;

    private String createdByName;

    private Integer partnerId;

    private Boolean isLco;

    private Long transactionNumber;



}
