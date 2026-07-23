package com.savbill.commonGateway.rabbitmq.messages;

import com.savbill.commonGateway.moules.PaymentConfig.model.SendPaymentConfigDTO;
import lombok.Data;

@Data
public class PaymentConfigMessage {

      SendPaymentConfigDTO paymentConfigDTO;
      String flag;
}
