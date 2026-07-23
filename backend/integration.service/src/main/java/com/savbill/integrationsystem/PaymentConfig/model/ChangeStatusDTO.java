package com.savbill.integrationsystem.PaymentConfig.model;

import lombok.Data;

@Data
public class ChangeStatusDTO {

    private Long paymentConfigId;

    private Boolean isActive;


}
