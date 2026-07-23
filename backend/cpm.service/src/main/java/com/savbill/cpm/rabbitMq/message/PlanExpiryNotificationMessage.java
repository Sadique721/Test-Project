package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanExpiryNotificationMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;


    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;


    public PlanExpiryNotificationMessage(String emailId, String mobileNumber, String customerUsername, String planName, String expiryDate, String ccEmail, String countryCode, Integer mvnoid, Integer buid, Integer custId) {

        this.setMessage("Plane Expiry Notification");
        this.setSourceName(sourceName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", mobileNumber);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoid);
        this.customerData.put("plan",planName);
        this.customerData.put("username", customerUsername);
        this.customerData.put("countryCode", countryCode);
        this.customerData.put("expiryDate" , expiryDate);
        this.customerData.put("buId",buid);
        this.customerData.put("custId",custId);
        this.customerData.put(RabbitMqConstants.BU_ID,null);
        if(Objects.nonNull(ccEmail) && ccEmail.length() > 0){
            this.customerData.put("altEmail" , ccEmail);
        }
        this.isEmailConfigured = true;
        this.isSmsConfigured = true;

    }

}
