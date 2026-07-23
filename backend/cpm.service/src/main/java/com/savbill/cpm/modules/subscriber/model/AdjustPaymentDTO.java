package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class AdjustPaymentDTO extends UpdateAbstarctDTO {
    private String paymentType;
    private Double amount;
    private LocalDate paymentDate;
    private String remarks;
}
