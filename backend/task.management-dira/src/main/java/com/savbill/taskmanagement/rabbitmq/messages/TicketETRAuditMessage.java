package com.savbill.taskmanagement.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketETRAuditMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private HashMap<String,Object> customerData = new HashMap<>();
}
