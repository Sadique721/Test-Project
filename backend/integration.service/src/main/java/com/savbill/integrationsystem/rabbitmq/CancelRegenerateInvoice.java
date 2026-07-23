package com.savbill.integrationsystem.rabbitmq;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelRegenerateInvoice {

    private Integer id;

    public CancelRegenerateInvoice(Integer obj){
        this.id=obj;
    }
}
