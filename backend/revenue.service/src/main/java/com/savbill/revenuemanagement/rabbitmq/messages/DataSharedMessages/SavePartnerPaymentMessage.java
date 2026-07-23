package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import com.savbill.revenuemanagement.core.service.partner.PartnerPayment;
import com.savbill.revenuemanagement.core.service.partner.PartnerPaymentDTO;
import lombok.Data;

@Data
public class SavePartnerPaymentMessage {
    private PartnerPayment partnerPayment;
    private PartnerPaymentDTO partnerPaymentDTO;
}
