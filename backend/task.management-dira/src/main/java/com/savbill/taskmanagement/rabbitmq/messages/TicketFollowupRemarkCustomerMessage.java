package com.savbill.taskmanagement.rabbitmq.messages;

import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketFollowupRemarkCustomerMessage {

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

    public TicketFollowupRemarkCustomerMessage(String customerMobileNumber, String customerEmailId, String message, TemplateNotification templateNotification, String customerName, String staffPersonName, String remark, Integer mvnoId, String ticketNumber, String teamStaff, Long buId) {
        this.setMessage("New Customer Inquiry: Immediate Attention Required");
        this.setSourceName(sourceName);
        this.setEmailTemplate(null);
        this.setSmsTemplate(null);
        this.setAppendUrl(null);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("userName",customerName);
        this.customerData.put("teamStaff",teamStaff);
        this.customerData.put("mobileNumber",customerMobileNumber);
        this.customerData.put("emailId",customerEmailId);
        this.customerData.put("ticketNumber",ticketNumber);
        this.customerData.put("staffPersonName",staffPersonName);
        if(remark==null){
            this.customerData.put("remark"," ");
        }else{
            this.customerData.put("remark",remark);
        }


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
