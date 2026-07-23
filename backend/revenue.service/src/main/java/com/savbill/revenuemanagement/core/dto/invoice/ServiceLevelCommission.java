package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceLevelCommission {
    private String planName;
    private Double partnerCommission;
    private Double royalty;
    private Double netCommission;
    private Double partnerTax;
    private Double tds;
    private Double payableCommission;
}
