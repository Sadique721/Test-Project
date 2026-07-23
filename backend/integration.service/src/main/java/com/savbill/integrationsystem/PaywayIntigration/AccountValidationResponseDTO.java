package com.savbill.integrationsystem.PaywayIntigration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class AccountValidationResponseDTO {

    private String accountNo;

    private double amount;

}

