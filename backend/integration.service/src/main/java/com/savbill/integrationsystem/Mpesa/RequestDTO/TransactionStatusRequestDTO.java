package com.savbill.integrationsystem.Mpesa.RequestDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionStatusRequestDTO {
    private String transactionId;
    private Integer identifierType;
}
