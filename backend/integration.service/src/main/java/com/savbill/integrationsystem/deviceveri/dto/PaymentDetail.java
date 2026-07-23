package com.savbill.integrationsystem.deviceveri.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail {
    private  Double latestPaymentAmount;
    private LocalDateTime latestPaymentDate;
}
