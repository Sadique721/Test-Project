package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import lombok.Data;

import java.util.List;

@Data
public class ListOfCreditDocForBatch {

    List<CreditDocMessage> creditDocMessageList;
}
