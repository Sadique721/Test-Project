package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.sql.Timestamp;

@Data
public class AddToWalletDTOResponse {

    private Integer status;

    private String message;

    private String accountNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss:SSSS")
    private Date timestamp = new Date();
}
