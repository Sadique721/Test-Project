//package com.savbill.inventorymanagement.rabbitmq.SharedMessages;
package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

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

    public InventoryThresholdMessage(String message, Integer mvnoId,
                                     String productName, String warehouseName,
                                     Long currentQty, String emailId,
                                     String sourceName, List<String> staffEmails) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.mvnoId = mvnoId;
        this.sourceName = sourceName;
        customerData.put("productName", productName);
        customerData.put("warehouseName", warehouseName);
        customerData.put("currentQty", currentQty);
        customerData.put("emailId", emailId);
        customerData.put("altEmailList", staffEmails);
        customerData.put("mvnoId", mvnoId);
    }
}
