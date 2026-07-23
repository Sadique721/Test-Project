package com.savbill.integrationsystem.PaymentConfig.model;


import com.savbill.integrationsystem.PaymentConfigMapping.entity.PaymentConfigMapping;
import lombok.Data;

import java.util.List;

@Data
public class SendPaymentConfigDTO {

    private Long paymentConfigId;

    private String paymentConfigName;

    private List<PaymentConfigMapping> paymentConfigMappingList;


    private Boolean isDelete;


    private Long mvnoId;

    private Boolean isActive;
}
