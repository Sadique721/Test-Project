package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveClientServMessge {
    Integer id;

    String name;

    String value;

    Integer mvnoId;
}
