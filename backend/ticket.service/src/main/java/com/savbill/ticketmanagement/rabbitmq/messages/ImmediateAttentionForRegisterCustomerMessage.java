package com.savbill.ticketmanagement.rabbitmq.messages;

import com.savbill.ticketmanagement.rabbitmq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImmediateAttentionForRegisterCustomerMessage {

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

    public ImmediateAttentionForRegisterCustomerMessage(String custName , String customerEmail, String subject, Integer mvnoId, Long buId) {
        this.setMessage("Query Acknowledgment");
        this.setSourceName(sourceName);
        this.setEmailTemplate(null);
        this.setSmsTemplate(null);
        this.setAppendUrl(null);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("userName",custName);
        this.customerData.put("subject",subject);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("emailId" , customerEmail);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
        this.isEmailConfigured = true;
        this.isSmsConfigured = true;
    }

}
