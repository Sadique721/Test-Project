package com.savbill.inventorymanagement.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFulfilmentMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private Integer mvnoId;
    private String requestDestinationName;
    private String requestSourcename;
    private String sourceName;
    private String quantity;
    private String productName;
    private String inwardNumber;
    private String emailId;
    private List<String> staffEmails = new ArrayList<>();
    private Map<String,Object> customerData = new HashMap<>();
    public InventoryFulfilmentMessage(String message, Integer mvnoId,
                                      String requestDestinationName, String requestSourcename,
                                      String sourceName, String quantity,
                                      String productName, String inwardNumber, String userName, String emailId,
                                      List<String> staffEmails) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.mvnoId = mvnoId;
        this.sourceName = sourceName;
        customerData.put("requestDestinationName", requestDestinationName);
        customerData.put("requestSourcename", requestSourcename);
        customerData.put("quantity", quantity);
        customerData.put("productName", productName);
        customerData.put("inwardNumber", inwardNumber);
        customerData.put("userName", userName);
        customerData.put("emailId", emailId);
        customerData.put("altEmailList", staffEmails);
        customerData.put("mvnoId", mvnoId);
    }
}
