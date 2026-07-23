package com.savbill.integrationsystem.SendBiilToGovernment.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendBillToGovernment {


    private String username;

    private String password;

    @JsonProperty(value = "seller_pan")
    private String sellerPan;

    @JsonProperty(value = "buyer_pan")
    private String buyerPan;

    @JsonProperty(value = "fiscal_year")
    private String fiscalYear;

    @JsonProperty(value = "buyer_name")
    private String buyerName;

    @JsonProperty(value = "invoice_number")
    private String invoiceNumber;

    @JsonProperty(value = "invoice_date")
    private String invoiceDate;

    @JsonProperty(value = "total_sales")
    private Double totalSales;

    @JsonProperty(value = "taxable_sales_vat")
    private Double taxableSalesVat;

    private Double vat;

    @JsonProperty(value = "excisable_amount")
    private Double excisableAmount;

    private Double excise;

    @JsonProperty(value = "taxable_sales_hst")
    private Double taxableSalesHst;

    private Double hst;

    @JsonProperty(value = "amount_for_esf")
    private Double amountForEsf;

    private Double esf;

    @JsonProperty(value = "export_sales")
    private Double exportSales;

    @JsonProperty(value = "tax_exempted_sales")
    private Double taxExemptedSales;

    private Boolean isrealtime;

    private String datetimeClient;

}
