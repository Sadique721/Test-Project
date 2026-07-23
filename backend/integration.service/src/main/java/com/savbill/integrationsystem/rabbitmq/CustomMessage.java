package com.savbill.integrationsystem.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class CustomMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Object data;
}
