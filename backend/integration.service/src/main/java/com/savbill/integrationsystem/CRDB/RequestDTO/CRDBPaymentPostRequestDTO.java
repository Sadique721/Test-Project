package com.savbill.integrationsystem.CRDB.RequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CRDBPaymentPostRequestDTO {

    @JsonProperty("payerName")
    private String payerName;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("amountType")
    private String amountType;

    @JsonProperty("paymentReference")
    private String paymentReference;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("paymentDesc")
    private String paymentDesc;

    @JsonProperty("payerID")
    private String payerID;

    @JsonProperty("payerMobile")
    private String payerMobile;

    @JsonProperty("transactionRef")
    private String transactionRef;

    @JsonProperty("transactionChannel")
    private String transactionChannel;

    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("token")
    private String token;

    @JsonProperty("checksum")
    private String checksum;

    @JsonProperty("institutionID")
    private Long institutionID;
}
