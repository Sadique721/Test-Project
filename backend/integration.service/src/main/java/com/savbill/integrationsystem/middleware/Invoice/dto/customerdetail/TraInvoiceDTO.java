package com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TraInvoiceDTO {

    @JsonProperty("invoice_date")
    @NotBlank(message = "Invoice date is mandatory")
    private String invoiceDate;

    @JsonProperty("invoice_number")
    @NotBlank(message = "Invoice number is mandatory")
    private String invoiceNumber;

    @JsonProperty("invoice_tin")
    @NotBlank(message = "Invoice TIN is mandatory")
    private String invoiceTin;

    @JsonProperty("customer_name")
    @NotBlank(message = "Customer name is mandatory")
    private String customerName;

    @JsonProperty("customer_tin")
    @NotBlank(message = "Customer TIN is mandatory")
    private String customerTin;

    @JsonProperty("customer_phone")
    @NotBlank(message = "Customer phone is mandatory")
    private String customerPhone;

    @JsonProperty("customer_vrn")
    @NotBlank(message = "Customer VRN is mandatory")
    private String customerVrn;

    @JsonProperty("passport_id")
    @NotBlank(message = "Passport ID is mandatory")
    private String passportId;

    @JsonProperty("driving_lic")
    @NotBlank(message = "Driving license is mandatory")
    private String drivingLic;

    @JsonProperty("customer_nid")
    @NotBlank(message = "Customer NID is mandatory")
    private String customerNid;

    @JsonProperty("gross_amount")
    @NotNull(message = "Gross amount is mandatory")
    private String grossAmount;

    @JsonProperty("vat_amount")
    @NotNull(message = "VAT amount is mandatory")
    private String vatAmount;

    @JsonProperty("grand_total")
    @NotNull(message = "Grand total is mandatory")
    private String grandTotal;

    @Override
    public String toString() {
        return "{" +
                "invoice_date:'" + invoiceDate + '\'' +
                ", invoice_number:'" + invoiceNumber + '\'' +
                ", invoice_tin='" + invoiceTin + '\'' +
                ", customer_name='" + customerName + '\'' +
                ", customer_tin='" + customerTin + '\'' +
                ", customer_phone='" + customerPhone + '\'' +
                ", customer_vrn='" + customerVrn + '\'' +
                ", passport_id='" + passportId + '\'' +
                ", driving_lic='" + drivingLic + '\'' +
                ", customer_nid='" + customerNid + '\'' +
                ", gross_amount='" + grossAmount + '\'' +
                ", vat_amount='" + vatAmount + '\'' +
                ", grand_total='" + grandTotal + '\'' +
                '}';
    }

}



