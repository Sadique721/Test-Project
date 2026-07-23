package com.savbill.revenuemanagement.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOnlinePaymentRevenueMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;

    private Map<String, Object> customerData = new HashMap<>();

}
