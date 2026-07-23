package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.PaymentConfig.model.SendPaymentConfigDTO;
import lombok.Data;

@Data
public class PaymentConfigMessage {

      SendPaymentConfigDTO paymentConfigDTO;
      String flag;
}
