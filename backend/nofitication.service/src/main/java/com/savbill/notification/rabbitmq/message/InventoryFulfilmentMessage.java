package com.savbill.notification.rabbitmq.message;

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
}
