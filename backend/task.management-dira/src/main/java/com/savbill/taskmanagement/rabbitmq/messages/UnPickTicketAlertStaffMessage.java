package com.savbill.taskmanagement.rabbitmq.messages;

import com.savbill.taskmanagement.core.dto.SendTicketDetailDTO;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public UnPickTicketAlertStaffMessage(String staffUserName , String staffMobileNumber, String staffEmail, String countryCode , String parentStaffEmail , List<SendTicketDetailDTO> sendTicketDetailDTOList , Integer mvnoId, Long buId) {

        this.setMessage("Open Ticket Alert");
        this.setSourceName("SAVBILL Ticket");
        this.setEmailTemplate(null);
        this.setSmsTemplate(null);
        this.setAppendUrl(null);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("userName",staffUserName);
  //      this.customerData.put("teamStaff",teamStaff);
        this.customerData.put("mobileNumber",staffMobileNumber);
        this.customerData.put("emailId",staffEmail);
       // this.customerData.put("ticketNumber",ticketNumber);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("countryCode" , countryCode);
        this.customerData.put("ticketData" , sendTicketDetailDTOList);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
        if(Objects.nonNull(parentStaffEmail)){
            this.customerData.put("altEmail",parentStaffEmail);
        }

        this.isEmailConfigured = true;
        this.isSmsConfigured = true;
    }

}
