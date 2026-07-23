package com.savbill.notification.savbilliwfnotification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketAttributeDTO {
    org.slf4j.Logger logger;
    private String id;
    private String label;
    private String name;
    private String index;
    private String dataType;
    private String dateFormat;
    private boolean optional;
    private String regex;
    private int regexGroupIndex;
    private Object value;
    private String offset;
    private String targetDateFormat;
    private boolean timestampId;
    private boolean indexed;
}
