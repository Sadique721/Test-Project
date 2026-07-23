package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDocIdsMessages {

    List<Integer> creditDocumentIds =new ArrayList<>();

    String action;
}
