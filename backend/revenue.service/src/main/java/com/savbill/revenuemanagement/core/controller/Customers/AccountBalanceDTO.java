package com.savbill.revenuemanagement.core.controller.Customers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountBalanceDTO {

    @JsonProperty("Package")
    private String Package;

    @JsonProperty("AccountNo")
    private String AccountNo;

    @JsonProperty("PaymentDue")
    private long PaymentDue;

    @JsonProperty("Balance")
    private Double Balance;

    @JsonProperty("mobileNumber")
    private String mobileNumber;
}
