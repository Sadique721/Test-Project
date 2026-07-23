package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.modules.PaymentConfig.model.SendPaymentConfigDTO;
import lombok.Data;

@Data
public class PaymentConfigMessage {

      SendPaymentConfigDTO paymentConfigDTO;
      String flag;
}
