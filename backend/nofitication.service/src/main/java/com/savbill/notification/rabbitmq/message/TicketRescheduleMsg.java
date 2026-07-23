package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRescheduleMsg {

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
    private String caseNumber;
    private String followUpDateAndTime;
}
