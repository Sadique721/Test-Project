package com.savbill.integrationsystem.InventoryItem;

import com.savbill.integrationsystem.rabbitmq.ApproveInventoryItemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ApproveInventoryItemService {
    @Autowired
    ApproveInventoryItemRepo approveInventoryItemRepo;

    public ApproveInventoryItem save(ApproveInventoryItemMessage message){
        try {
            if (message.getCustomerInventoryData() != null) {
                ApproveInventoryItem approveInventoryItem = new ApproveInventoryItem(message.getCustomerInventoryData());
                return approveInventoryItemRepo.save(approveInventoryItem);
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
