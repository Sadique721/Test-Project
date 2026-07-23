package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDebitDocMessage {
    List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
}
