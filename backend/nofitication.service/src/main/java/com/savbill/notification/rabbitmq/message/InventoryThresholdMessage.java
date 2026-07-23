//package com.savbill.inventorymanagement.rabbitmq.SharedMessages;
package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryThresholdMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private Integer mvnoId;
    private String productName;
    private String warehouseName;
    private Long currentQty;
    private String emailId;
    private String sourceName;
    private List<String> staffEmails = new ArrayList<>();
    private Map<String,Object> customerData = new HashMap<>();
}
