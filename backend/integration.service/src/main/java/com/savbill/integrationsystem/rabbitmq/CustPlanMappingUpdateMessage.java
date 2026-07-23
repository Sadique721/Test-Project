package com.savbill.integrationsystem.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustPlanMappingUpdateMessage {
    List<Integer> customerPlanMappingIds;
    Integer debitDocumentId;
}
