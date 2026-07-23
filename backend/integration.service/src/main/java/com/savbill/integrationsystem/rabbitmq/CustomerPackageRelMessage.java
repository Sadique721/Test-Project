package com.savbill.integrationsystem.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerPackageRelMessage {

    private static final String SAVBILL_API_GATEWAY = "SAVBILL_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String END_DATE = "endDate";
    private static final String EXPIRY_DATE = "expiryDate";
    private static final String CUST_PLAN_STATUS = "custPlanStatus";
    private static final String CUST_SERVICE_MAPPING_ID = "custServiceMappingId";
    private String operation;

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Map<String, Object> data;

    public CustomerPackageRelMessage() {

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's used data updates";
    }






}
