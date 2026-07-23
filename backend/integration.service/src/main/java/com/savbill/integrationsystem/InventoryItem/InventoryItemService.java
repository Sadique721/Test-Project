package com.savbill.integrationsystem.InventoryItem;

import com.savbill.integrationsystem.rabbitmq.ItemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemService {
    @Autowired
    IntentoryItemRepo intentoryItemRepo;


    public IntentoryItem save(ItemMessage message){
        try {
            if (message.getCustomerInventoryData() != null) {
                IntentoryItem intentoryItem = new IntentoryItem(message.getCustomerInventoryData());
                IntentoryItem mappping = intentoryItemRepo.save(intentoryItem);
                return mappping;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
