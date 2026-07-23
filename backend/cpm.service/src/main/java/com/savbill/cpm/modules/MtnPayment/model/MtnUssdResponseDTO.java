package com.savbill.cpm.modules.MtnPayment.model;


import lombok.Data;

@Data
public class MtnUssdResponseDTO {
    private String statusCode;

    private String statusMessage;

    private String transactionId;

    private MtnUssdDataResponse data;
}
