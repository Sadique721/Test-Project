package com.savbill.commonGateway.moules.PaymentConfig.model;

import lombok.Data;

@Data
public class ChangeStatusDTO {

    private Long paymentConfigId;

    private Boolean isActive;


}
