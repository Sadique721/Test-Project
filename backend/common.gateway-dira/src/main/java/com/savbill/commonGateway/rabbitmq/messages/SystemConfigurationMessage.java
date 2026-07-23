package com.savbill.commonGateway.rabbitmq.messages;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemConfigurationMessage {

    private Long id;

    private String name;

    private String value;

    private Integer mvnoId;

    public SystemConfigurationMessage(Long id, String name, String value, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.mvnoId = mvnoId;
    }
}
