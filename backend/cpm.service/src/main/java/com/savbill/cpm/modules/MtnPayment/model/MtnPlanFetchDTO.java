package com.savbill.cpm.modules.MtnPayment.model;

import lombok.Data;

@Data
public class MtnPlanFetchDTO {

    private String service;

    private String mobileNumber;

    private String transactionId;

    private String username;

    private String password;
}
