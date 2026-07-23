package com.savbill.commonGateway.kafka;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GeneratePasswordDto {
    private String genPassUrl;
    private String username;
    private String eventName;
    private Long eventId;
    private Map<String, Object> manualMailContent = new HashMap();
    private String applicationName;
    private String email;
}
