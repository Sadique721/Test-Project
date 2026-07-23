package com.savbill.inventorymanagement.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

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
    private Integer mvnoId;
    private Map<String,Object> customerData = new HashMap<>();

    public InventoryApprovalSuccessMsg(String employeeName, String assetSpecification,String assignDate,String mobileNumber,String emailId,String sourceName,String status,String message,Integer mvnoId,Long buId) {
        this.setMessage(message);
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        this.setRegistrationStatus(status);

        customerData.put("employeeName", employeeName);
        //customerData.put("assetId", assetId);
        //customerData.put("serialNumber", serialNumber);
        customerData.put("assetsSpecification", assetSpecification);
        customerData.put("assignDate", assignDate);
        customerData.put("mobileNumber", mobileNumber);
        customerData.put("emailId", emailId);
        customerData.put("mvnoId", mvnoId);
        if (Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if (Objects.isNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, null);
        }
    }
}
