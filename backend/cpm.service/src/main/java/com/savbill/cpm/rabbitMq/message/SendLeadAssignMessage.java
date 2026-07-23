package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.pojo.api.LeadMgmtWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendLeadAssignMessage {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadMgmtWfDTO;

    public SendLeadAssignMessage(LeadMgmtWfDTO leadMgmtWfDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead management approval";
        this.leadMgmtWfDTO = leadMgmtWfDTO;
    }
}
