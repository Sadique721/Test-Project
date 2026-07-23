package com.savbill.cpm.modules.SubscriberUpdates.Utils;

import java.util.List;

import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.SubscriberUpdates.model.SubscriberUpdateDTO;
import com.savbill.cpm.modules.SubscriberUpdates.service.SubscriberUpdateService;
import com.savbill.cpm.spring.SpringContext;

public class SubscriberUpdateUtils {

    public static void updateSubscriber(String oldValue, String newValue, String operation, Customers customers, String remarks, String entityName) throws Exception {
        SubscriberUpdateService service = SpringContext.getBean(SubscriberUpdateService.class);
        SubscriberUpdateDTO subscriberUpdateDTO = new SubscriberUpdateDTO(oldValue, newValue, operation, remarks, customers, entityName);
        service.saveEntity(subscriberUpdateDTO);
    }

    public static String customString(List<String> list) {
        StringBuilder builder = new StringBuilder("");
        list.forEach(data -> {
            if (data != null) {
                builder.append("," + data);
            }
        });
        if (builder.toString().length() > 1) {
            builder.delete(0, 1);
        }
        return builder.toString();
    }
}
