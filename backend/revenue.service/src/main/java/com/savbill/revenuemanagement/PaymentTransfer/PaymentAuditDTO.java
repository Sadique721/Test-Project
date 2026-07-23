package com.savbill.revenuemanagement.PaymentTransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentAuditDTO {
    private String fromName;
    private String toName;
    private Double amount;
    private LocalDateTime createdDate;
}
