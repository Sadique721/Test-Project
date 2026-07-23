package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.Customers;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerEndDateUpdateMessage {
    private String messageId;
    private String message;
    private Date messageDate;

    private static final String ID = "id";

    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";

    private static final String STARTDATE = "startDate";

    private static final String ENDDATE = "endDate";

    private static final String EXPIRYDATE = "expiryDate";

    private Map<String,Object> customerData;

    public CustomerEndDateUpdateMessage(Customers customers , String endDate) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, customers.getId().toString());
        map.put(MVNO_ID, customers.getMvnoId().toString());
        map.put(ENDDATE,endDate);
        map.put(USERNAME , customers.getUsername());
        this.customerData = map;
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's status updates";

    }
}
