package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.InventoryItem.ApproveInventoryItem;
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
public class ApproveInventoryItemMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;

    private Map<String,Object> customerInventoryData = new HashMap<>();
    public ApproveInventoryItemMessage(ApproveInventoryItem item, String message) {
        this.setMessage(message);
        this.setMessageDate(new Date());
        this.messageId = UUID.randomUUID().toString();
        customerInventoryData.put("id",item.getId());
        customerInventoryData.put("name",item.getName());
        customerInventoryData.put("macAddress",item.getMacAddress());
        customerInventoryData.put("serialNumber",item.getSerialNumber());
        customerInventoryData.put("mvnoId",item.getMvnoId());
        customerInventoryData.put("condition",item.getCondition());
        customerInventoryData.put("productId",item.getProductId());
        customerInventoryData.put("currentInwardId",item.getCurrentInwardId());
        customerInventoryData.put("ownerId",item.getOwnerId());
        customerInventoryData.put("ownerType",item.getOwnerType());
        customerInventoryData.put("warrantyPeriod",item.getWarrantyPeriod());
        customerInventoryData.put("warranty",item.getWarranty());
        customerInventoryData.put("currentInwardType",item.getCurrentInwardType());
        customerInventoryData.put("itemStatus",item.getItemStatus());
        customerInventoryData.put("remainingDays",item.getRemainingDays());
        customerInventoryData.put("isDeleted",item.getIsDeleted());
        customerInventoryData.put("ownershipType",item.getOwnershipType());
        customerInventoryData.put("externalItemId",item.getExternalItemId());
        customerInventoryData.put("intransiantWarrenty",item.getIntransiantWarrenty());
        customerInventoryData.put("intransiantWarrentyStatus",item.getIntransiantWarrentyStatus());
        customerInventoryData.put("intransiantOwnership",item.getIntransiantOwnership());
        customerInventoryData.put("expireDate",item.getExpireDate());
        customerInventoryData.put("intransiantexpireDate",item.getIntransiantexpireDate());
    }
}
