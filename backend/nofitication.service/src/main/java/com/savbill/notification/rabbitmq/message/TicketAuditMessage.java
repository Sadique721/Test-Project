package com.savbill.notification.rabbitmq.message;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Data
public class TicketAuditMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private Map<String,Object> customerData = new HashMap<>();

    public TicketAuditMessage(Map<String, Object> customerData) {
        this.customerData = customerData;
    }

}
