package com.savbill.revenuemanagement.autoassign;

import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoAdjustPaymentRequest {
    private CustomerBillingMessage customerBillingMessage;
    private DebitDocument debitDocument;
    private TrialDebitDocument trialDebitDocument;
}

