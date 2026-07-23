package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.Customers;
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
public class CustomerUpdateMessage {
    private String messageId;
    private String message;
    private Date messageDate;

    private static final String ID = "id";

    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";
    private static final String STATUS = "status";

    private static final String STARTDATE = "startDate";

    private static final String ENDDATE = "endDate";

    private static final String EXPIRYDATE = "expiryDate";

    private static final String MAXCONCURRENTSESSION = "maxconcurrentsession";

    private Map<String,Object> customerData;

    public CustomerUpdateMessage(Customers customers) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, customers.getId());
        map.put(MVNO_ID, customers.getMvnoId());
        map.put(MAXCONCURRENTSESSION, customers.getMaxconcurrentsession());
        map.put(STATUS, customers.getStatus());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's Concurrency updates";
        this.customerData = map;

    }
}
