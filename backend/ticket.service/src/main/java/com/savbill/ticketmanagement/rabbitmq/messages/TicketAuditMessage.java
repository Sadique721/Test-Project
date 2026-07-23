package com.savbill.ticketmanagement.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
