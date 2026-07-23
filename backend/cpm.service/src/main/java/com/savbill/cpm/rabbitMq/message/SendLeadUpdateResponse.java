package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.pojo.api.LeadMgmtWfDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class SendLeadUpdateResponse {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private HashMap<String, Object> leadFlowApproverUpdatedData;

    public SendLeadUpdateResponse(LeadMgmtWfDTO leadMgmtWfDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = "APIGATEWAY";
        this.message = "Getting suitable staff for lead management approval";
    }
}
