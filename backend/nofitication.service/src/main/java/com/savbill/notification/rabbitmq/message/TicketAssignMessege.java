package com.savbill.notification.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketAssignMessege {

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

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String parentStaffPersonName;

    private String staffPersonName;

    private String eventName;

    private String assigndatetime;

    private String caseNumber;


//    private TicketTatAudits tatAudits;

    private Integer caseId;


    private String caseStatus;



    private String tatAction;


    private Integer tatTime;


    private String tatUnit;


    private Integer slaTime;


    private String slaUnit;


    private String tatStartTime;



    private String tatMessage;


    private Integer assignStaffId;


    private Integer assignStaffParentId;


    private String caseLevel;


    private String notificationFor;


    private String isTatBreached;


    private String isSlaBreached;

}
