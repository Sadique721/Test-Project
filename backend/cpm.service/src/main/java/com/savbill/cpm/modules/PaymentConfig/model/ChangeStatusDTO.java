package com.savbill.cpm.modules.PaymentConfig.model;

import lombok.Data;

@Data
public class ChangeStatusDTO {

    private Long paymentConfigId;

    private Boolean isActive;


}
