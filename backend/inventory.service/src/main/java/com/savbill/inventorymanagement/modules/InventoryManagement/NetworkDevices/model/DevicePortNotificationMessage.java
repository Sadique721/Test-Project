package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
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

    public DevicePortNotificationMessage(String ownerType,String ownerName,String staffName,String deviceName,String consumePercentage,String mobileNumber,String emailId,String sourceName,String message,Integer mvnoId,Long buId) {
        this.setMessage(message);
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;

        staffData.put("employeeName", staffName);
        staffData.put("ownerType",ownerType);
        staffData.put("ownerName",ownerName);
        staffData.put("deviceName", deviceName);
        staffData.put("consumePercentage", consumePercentage);
        staffData.put("mobileNumber", mobileNumber);
        staffData.put("emailId", emailId);
        staffData.put("mvnoId", mvnoId);
        if (Objects.nonNull(buId))
            staffData.put(RabbitMqConstants.BU_ID, buId);
        if (Objects.isNull(buId))
            staffData.put(RabbitMqConstants.BU_ID, null);
    }
}
