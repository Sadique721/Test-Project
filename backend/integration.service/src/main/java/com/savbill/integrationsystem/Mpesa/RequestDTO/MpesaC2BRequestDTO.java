package com.savbill.integrationsystem.Mpesa.RequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MpesaC2BRequestDTO {
    @JsonProperty("TransactionType")
    private String transactionType;
    @JsonProperty("TransID")
    private String transID;
    @JsonProperty("TransTime")
    private String transTime;
    @JsonProperty("TransAmount")
    private BigDecimal transAmount;
    @JsonProperty("BusinessShortCode")
    private String businessShortCode;
    @JsonProperty("BillRefNumber")
    private String billRefNumber;
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    @JsonProperty("OrgAccountBalance")
    private String orgAccountBalance;
    @JsonProperty("ThirdPartyTransID")
    private String thirdPartyTransID;
    @JsonProperty("MSISDN")
    private String msisdn;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("MiddleName")
    private String middleName;
    @JsonProperty("LastName")
    private String lastName;

}
