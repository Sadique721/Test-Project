package com.savbill.revenuemanagement.core.entity.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeDetails {

    String chargeType;
    double tax;
    double total;
    double price;

    public ChargeDetails(double tax, double total, double price) {
        this.tax = tax;
        this.total = total;
        this.price = price;
    }
}
