package com.savbill.integrationsystem.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UuidMessageSend {

    Integer customerServiceMappingId;
    String uuid;

}
