package com.savbill.radius.kafka.message;

import com.savbill.radius.kafka.CustomMessage;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class CustServiceChargeIPDtlsMessage {

    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String CUST_SERVICE_MAPPING_ID = "custservicemappingid";
    private static final String STATIC_IP_ADDRESS = "static_ip_address";
    private static final String STATIC_IP_START_DATE = "static_ip_start_date";
    private static final String STATIC_IP_END_DATE = "static_ip_end_date";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;
}
