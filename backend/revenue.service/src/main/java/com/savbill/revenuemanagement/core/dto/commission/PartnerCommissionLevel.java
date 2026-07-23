package com.savbill.revenuemanagement.core.dto.commission;

import com.savbill.revenuemanagement.core.entity.partner.Partner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerCommissionLevel {
    private Partner partner;
    private Double commission;
    private Double commissionPercentage;
}
