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

public class PaymentLinkMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;

    private String mobileNumber;
    private String emailId;

    private String customerName ;
    private String currencySymbol;
    private Double paymentAmount;
    private String url1;
    private String url2;
    private Integer mvnoId;
    private String appendUrl;


    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private Map<String,Object> customerData = new HashMap<>();

}
