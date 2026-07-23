package com.savbill.integrationsystem.PaywayIntigration;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountBalanceDTO {
    private List<String> Package;
    private String AccountNo;
    private long PaymentDue;
    private Double Balance;

}
