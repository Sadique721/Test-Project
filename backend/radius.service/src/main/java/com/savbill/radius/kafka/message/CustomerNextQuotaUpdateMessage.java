package com.savbill.radius.kafka.message;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerNextQuotaUpdateMessage {
    Integer custId;
    LocalDate nextQuotaResetDate;
    LocalDate nextBillDate;
}
