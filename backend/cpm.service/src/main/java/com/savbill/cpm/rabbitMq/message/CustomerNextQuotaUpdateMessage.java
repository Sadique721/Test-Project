package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.common.Customers;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerNextQuotaUpdateMessage {
    Integer custId;
    LocalDate nextQuotaResetDate;
    LocalDate nextBillDate;

    public CustomerNextQuotaUpdateMessage(Customers customers) {
        this.custId = customers.getId();
        this.nextQuotaResetDate = customers.getNextQuotaResetDate();
        this.nextBillDate = customers.getNextBillDate();
    }
}
