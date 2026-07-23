package com.savbill.revenuemanagement.rabbitmq.messages.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInventoryRevenueMessage {
    private Map<String,Object> customerInventoryData = new HashMap<>();

}
