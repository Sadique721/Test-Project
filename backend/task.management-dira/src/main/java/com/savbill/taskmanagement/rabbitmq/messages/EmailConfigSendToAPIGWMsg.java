package com.savbill.taskmanagement.rabbitmq.messages;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailConfigSendToAPIGWMsg {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Map<String, Object> emailConfigData;


}
