package com.savbill.radius.services.impl;

import com.savbill.radius.entity.CustReplyItem;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.repository.CustReplyItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustReplyItemServiceImpl {

    @Autowired
    private CustReplyItemRepository custReplyItemRepository;

    public CustReplyItem save(CustomMessage message){
        try {
            if (message.getData() != null) {
                CustReplyItem custReplyItem = new CustReplyItem(message);
                CustReplyItem custReplyItemSave = custReplyItemRepository.save(custReplyItem);
                return custReplyItemSave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
