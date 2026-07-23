package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PendingAmountResponse {
    private Integer status;

    private String message;

    private String accountNo;

    private Double pendingAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss:SSSS")
    private Date timestamp = new Date();
}
