package com.savbill.inventorymanagement.rabbitmq;

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

    public InventoryRequestMessage(String message, Integer mvnoId,
                                   String requestTo, String requester,
                                   String sourceName, String onBehalfOf, String userName,
                                   String emailId, List<String> staffEmails) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.mvnoId = mvnoId;
        this.sourceName = sourceName;
        customerData.put("requestTo", requestTo);
        customerData.put("requester", requester);
        customerData.put("onBehalfOf", onBehalfOf);
        customerData.put("userName", userName);
        customerData.put("emailId", emailId);
        customerData.put("altEmailList", staffEmails);
        customerData.put("mvnoId", mvnoId);
    }
}
