package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private Integer mvnoId;
    private String requestTo;
    private String requester;
    private String sourceName;
    private String onBehalfOf;
    private String emailId;
    private List<String> staffEmails = new ArrayList<>();
    private Map<String,Object> customerData = new HashMap<>();
}
