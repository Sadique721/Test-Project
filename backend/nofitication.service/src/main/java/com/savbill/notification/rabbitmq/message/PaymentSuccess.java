package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class PaymentSuccess {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;

    private String customerName ;
    private String currencySymbol;
    private Double paymentAmount;
    private String paymentMode;
    private String mobileNumber;
    private String emailId;

    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String appendUrl;

    private Integer mvnoId;

    private Map<String,Object> customerData = new HashMap<>();
    private Integer userId;
    private String username ;
    private String reciptNo;
    private String paymentDate;
}
