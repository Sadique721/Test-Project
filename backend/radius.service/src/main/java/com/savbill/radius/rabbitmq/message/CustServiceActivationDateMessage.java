package com.savbill.radius.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustServiceActivationDateMessage {
    private Integer id;
    private Long serviceActivationDate;
}
