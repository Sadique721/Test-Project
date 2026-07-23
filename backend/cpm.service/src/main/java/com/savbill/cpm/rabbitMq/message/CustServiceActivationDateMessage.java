package com.savbill.cpm.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustServiceActivationDateMessage {
    private Integer id;
    private Long serviceActivationDate;
}
