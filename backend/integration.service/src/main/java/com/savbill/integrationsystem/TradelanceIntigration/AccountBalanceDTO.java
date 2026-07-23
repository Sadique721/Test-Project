package com.savbill.integrationsystem.TradelanceIntigration;

import lombok.Data;

@Data
public class AccountBalanceDTO {
    private String Package;
    private String AccountNo;
    private long PaymentDue;
    private Double Balance;
    private String mobileNumber;
}
