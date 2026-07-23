package com.savbill.integrationsystem.PaymentIntegration.Model;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelcomGeneralDTO {
    private SelcomPayPayment selcomPayPayment;
    private CustomerPaymentDTO customerPaymentDTO;
}
