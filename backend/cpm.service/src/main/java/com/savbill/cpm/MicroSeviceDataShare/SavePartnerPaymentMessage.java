package com.savbill.cpm.MicroSeviceDataShare;

import com.savbill.cpm.modules.PartnerLedger.domain.PartnerPayment;
import com.savbill.cpm.modules.PartnerLedger.model.PartnerPaymentDTO;
import lombok.Data;

@Data
public class SavePartnerPaymentMessage {
    private PartnerPayment partnerPayment;
    private PartnerPaymentDTO partnerPaymentDTO;
}
