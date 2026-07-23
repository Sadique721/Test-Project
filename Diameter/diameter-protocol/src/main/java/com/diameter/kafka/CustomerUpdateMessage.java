package com.diameter.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateMessage {
    private String messageId;
    private String message;
    //private Date ;

    private static final String ID = "id";

    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";
    private static final String STATUS = "status";

    private static final String STARTDATE = "startDate";

    private static final String ENDDATE = "endDate";

    private static final String EXPIRYDATE = "expiryDate";

    private Map<String,Object> customerData;

}
