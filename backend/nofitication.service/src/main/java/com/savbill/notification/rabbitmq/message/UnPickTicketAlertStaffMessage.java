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

public class UnPickTicketAlertStaffMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String username;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;
    private String countryCode = "+91";
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    private String staffPersonName;
    private String ticketNumber;
    private String parentStaffPersonName;
    private String remark ;
    private Map<String,Object> customerData = new HashMap<>();



}
