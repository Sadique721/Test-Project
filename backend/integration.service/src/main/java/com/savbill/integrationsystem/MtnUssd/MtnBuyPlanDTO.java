package com.savbill.integrationsystem.MtnUssd;

import lombok.Data;

@Data
public class MtnBuyPlanDTO {

    private Integer planId;

    private String mobileNumber;

    private String transactionId;

    private String username;

    private String password;
}
