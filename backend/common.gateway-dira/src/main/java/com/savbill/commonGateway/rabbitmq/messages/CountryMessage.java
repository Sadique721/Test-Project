package com.savbill.commonGateway.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryMessage {

    private Integer id;

    private String name;

    private String status;

    private Boolean isDelete;
    private Integer mvnoId;
}
