package com.savbill.revenuemanagement.rabbitmq.messages;


import com.savbill.revenuemanagement.rabbitmq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInvoiceMessage {

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
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    private String staffPersonName;
    private String ticketNumber;
    private String parentStaffPersonName;
    private String remark ;
    private Map<String,Object> customerData = new HashMap<>();

    public CustomerInvoiceMessage(String customerMobileNumber , String countryCode, String customerEmailId,String customerName,String filename, String filepath,Integer mvnoId, Long buId) {

        this.setMessage("Customer Invoice Message");
        this.setSourceName(sourceName);
        this.setEmailTemplate("");
        this.setSmsTemplate("");
        this.setAppendUrl("");
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("userName",customerName);;
        this.customerData.put("mobileNumber",customerMobileNumber);
        this.customerData.put("emailId",customerEmailId);
        this.customerData.put("countryCode" , countryCode);
        this.customerData.put("fileName",filename);
        this.customerData.put("filePath",filepath);
        this.customerData.put("mvnoId",mvnoId);
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
