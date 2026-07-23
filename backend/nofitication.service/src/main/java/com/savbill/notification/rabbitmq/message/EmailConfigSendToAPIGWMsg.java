package com.savbill.notification.rabbitmq.message;

import com.savbill.notification.entity.EmailConfig;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = EmailConfigSendToAPIGWMsg.class)
public class EmailConfigSendToAPIGWMsg {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Map<String, Object> emailConfigData;

    private static final String TITLE = "title";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SMTPAUTH = "smtpauth";
    private static final String AUTHTYPE = "authtype";
    private static final String HOSTSERVER = "hostserver";
    private static final String PORT = "port";
    private static final String MVNOID = "mvnoid";
    private static final String BUID="buid";

    public EmailConfigSendToAPIGWMsg(EmailConfig emailConfig){
        Map<String, Object> map = new HashMap<>();
        map.put(USERNAME , emailConfig.getUserName());
        map.put(PASSWORD , emailConfig.getPassword());
        map.put(SMTPAUTH, "1");
        map.put(AUTHTYPE , emailConfig.getAuthType());
        map.put(HOSTSERVER , emailConfig.getHostServer());
        map.put(PORT , emailConfig.getPort());
        map.put(MVNOID , emailConfig.getMvnoId());
        map.put(BUID , emailConfig.getBuId());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Email config send to API Gateway";
        this.emailConfigData = map;
        this.sourceName = "NOTIFICATION";
    }

}
