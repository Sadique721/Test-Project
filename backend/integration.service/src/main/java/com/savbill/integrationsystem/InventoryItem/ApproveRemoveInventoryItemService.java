package com.savbill.integrationsystem.InventoryItem;

import com.savbill.integrationsystem.rabbitmq.ApproveRemoveInventoryItemRequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApproveRemoveInventoryItemService {

    @Autowired
    ApproveRemoveInventoryItemRepo approveRemoveInventoryItemRepo;

    public ApproveRemoveInventoryItem save(ApproveRemoveInventoryItemRequestMessage message){
        try {
            if (message.getCustomerInventoryData() != null) {
                ApproveRemoveInventoryItem ApproveRemoveInventoryItem = new ApproveRemoveInventoryItem(message.getCustomerInventoryData());
                return approveRemoveInventoryItemRepo.save(ApproveRemoveInventoryItem);

            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
