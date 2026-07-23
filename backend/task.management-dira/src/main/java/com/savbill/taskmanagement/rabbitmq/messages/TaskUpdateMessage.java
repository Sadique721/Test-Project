package com.savbill.taskmanagement.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateMessage {
    private String message;
    Map<String ,Object> data=new HashMap<>();
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;


}
