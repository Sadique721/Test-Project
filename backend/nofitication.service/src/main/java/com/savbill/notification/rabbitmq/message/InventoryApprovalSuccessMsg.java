package com.savbill.notification.rabbitmq.message;

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
public class InventoryApprovalSuccessMsg {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String employeeName;
    private String assetsId;
    private String serialNumber;
    private String assetsSpecification;
    private String assignDate;
    private String emailId;
    private String mobileNumber;
    private String registrationStatus;
    private Map<String,Object> customerData = new HashMap<>();

    private Integer mvnoId;
}
