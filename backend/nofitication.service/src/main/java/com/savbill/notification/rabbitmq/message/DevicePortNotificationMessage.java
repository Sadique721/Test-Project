package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class DevicePortNotificationMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String staffName;
    private String staffEmailId;
    private String productName;
    private String productCategoryName;
    private String warrantyRemainingDays;
    private String employeeName;
    private String assetsId;
    private String serialNumber;
    private String emailId;
    private String mobileNumber;
    private Integer mvnoId;
    private Map<String,Object> staffData = new HashMap<>();
}
