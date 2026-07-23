package com.savbill.notification.rabbitmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustApprovalFailMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private String approverTeam;

    private String username;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;



    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured = true;
    private boolean isEmailConfigured = true;

//    public CustApprovalFailMessage(Customers customersVo, String message, TemplateNotification template, String sourceName, String approverTeam){
//        Map<String,Object> map = new HashMap<>();
//        map.put(RabbitMqConstants.USER_NAME, customersVo.getUsername());
//        map.put(RabbitMqConstants.MOBILE_NUMBER, customersVo.getMobile());
//        map.put(RabbitMqConstants.EMAIL_ADDRESS, customersVo.getEmail());
//        map.put(RabbitMqConstants.CAF_APPROVAL_STATUS,customersVo.getCafApproveStatus());
//        map.put(RabbitMqConstants.NEXT_CAF_APPROVER,customersVo.getNextCafApprover());
//        map.put(RabbitMqConstants.PREVIOUS_CAF_APPROVER,customersVo.getPreviousCafApprover());
//
//        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.setApproverTeam(approverTeam);
//        this.sourceName = sourceName;
//
//
//    }



//    public CustApprovalFailMessage(String username, String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template, String sourceName, String approverTeam, Boolean isEmailConfigured, Boolean isSmsConfigured ){
//
//        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        //this.setApproverTeam(approverTeam);
//        this.sourceName = sourceName;
//
//        customerData.put("username",username);
//        customerData.put("mobileNumber",mobileNumber);
//        customerData.put("emailId",emailId);
//        customerData.put("mvnoId",mvnoId);
//        customerData.put("approverTeam",approverTeam);
//        this.isEmailConfigured = isEmailConfigured;
//        this.isSmsConfigured = isSmsConfigured;
//
////        this.username = username;
////        this.emailId = emailId;
////        this.mobileNumber = mobileNumber;
//    }
}
