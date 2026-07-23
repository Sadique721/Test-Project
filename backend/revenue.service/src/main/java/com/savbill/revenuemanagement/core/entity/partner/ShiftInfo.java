package com.savbill.revenuemanagement.core.entity.partner;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShiftInfo {
    private Boolean isInvoiceClear;

    private Double transferBalance=0.0;

    private Double transferCommission=0.0;
}
