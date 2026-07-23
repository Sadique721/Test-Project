package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendApproverForLeadMsg {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO LeadFlowApproverData;

//    public SendApproverForLeadMsg(HashMap<String, Long> leadFlowApproverData) {
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.message = "Getting suitable staff for lead management approval";
//        LeadFlowApproverData = leadFlowApproverData;
//    }
}

