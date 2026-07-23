package com.savbill.notification.rabbitmq.message;

import lombok.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustTicketStatusMessage {

    private String message;
    private String messageId;
    private Date messageDate;

    private String username;

    private Integer ticketnumber;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;

    private Map<String, Object> customerData = new HashMap<>();
    public Map<String, Object> getCustomerData() {
        return customerData;
    }

    private boolean isSmsConfigured=true;
    private boolean isEmailConfigured=true;

    private String sourceName;

    public String getSourceName() {
        return sourceName;
    }

    private String emailTemplate;

    public String getEmailTemplate() {
        return emailTemplate;
    }

    private String smsTemplate;
    public String getSmsTemplate() {
        return smsTemplate;
    }

    private String appendUrl;

    public String getAppendUrl() {
        return appendUrl;
    }
}
