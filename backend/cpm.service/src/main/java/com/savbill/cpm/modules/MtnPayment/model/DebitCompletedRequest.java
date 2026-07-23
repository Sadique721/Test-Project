package com.savbill.cpm.modules.MtnPayment.model;

import lombok.Data;

@Data
public class DebitCompletedRequest {

    private String transactionid;

    private String externaltransactionid;

}
