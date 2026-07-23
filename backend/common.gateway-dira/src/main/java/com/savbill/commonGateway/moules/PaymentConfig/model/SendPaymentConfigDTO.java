package com.savbill.commonGateway.moules.PaymentConfig.model;

import com.savbill.commonGateway.moules.PaymentConfigMapping.entity.PaymentConfigMapping;
import lombok.Data;

import java.time.LocalDateTime;
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
