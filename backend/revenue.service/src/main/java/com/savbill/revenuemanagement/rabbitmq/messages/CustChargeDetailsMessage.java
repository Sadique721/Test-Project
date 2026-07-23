package com.savbill.revenuemanagement.rabbitmq.messages;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CustChargeDetailsMessage {

    private Integer custChargeId;
    private LocalDate nextInstallmentDate;
    private LocalDate lastInstallmentDate;
    private Integer installmentNo;

    public CustChargeDetailsMessage(Integer custChargeId, LocalDate nextInstallmentDate, LocalDate lastInstallmentDate, Integer installmentNo){
        this.custChargeId = custChargeId;
        this.nextInstallmentDate = nextInstallmentDate;
        this.lastInstallmentDate = lastInstallmentDate;
        this.installmentNo = installmentNo;
    }

}
