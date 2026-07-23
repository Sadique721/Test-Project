package com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty;

import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import lombok.Data;

import java.util.*;


@Data
public class WarrantyNotificationMessage {
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

    public WarrantyNotificationMessage(String employeeName,String assetId,String serialNumber,String productName,String productCategoryName,String remainingDays,String mobileNumber,String emailId,String sourceName,String message,Integer mvnoId,Long buId) {
        this.setMessage(message);
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;

        staffData.put("employeeName", employeeName);
        staffData.put("assetId", assetId);
        staffData.put("productName", productName);
        staffData.put("productCategoryName", productCategoryName);
        staffData.put("serialNumber", serialNumber);
        staffData.put("remainingDays", remainingDays);
        staffData.put("mobileNumber", mobileNumber);
        staffData.put("emailId", emailId);
        staffData.put("mvnoId", mvnoId);
        if (Objects.nonNull(buId))
            staffData.put(RabbitMqConstants.BU_ID, buId);
        if (Objects.isNull(buId))
            staffData.put(RabbitMqConstants.BU_ID, null);
    }
}
