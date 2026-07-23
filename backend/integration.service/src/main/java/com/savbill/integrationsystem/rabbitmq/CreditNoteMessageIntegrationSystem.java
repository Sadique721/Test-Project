package com.savbill.integrationsystem.rabbitmq;

import lombok.Data;

import java.util.Map;

@Data
public class CreditNoteMessageIntegrationSystem {
    Map<String, Double> data;

    String documentNumber;
    Integer creditDocId;
    Double amount;
    Integer customerId;
}
