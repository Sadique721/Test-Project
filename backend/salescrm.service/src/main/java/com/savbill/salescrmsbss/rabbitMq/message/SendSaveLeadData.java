package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSaveLeadData {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadMgmtWfDTO;

    public SendSaveLeadData(LeadMgmtWfDTO leadMgmtWfDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead management approval";
        this.leadMgmtWfDTO = leadMgmtWfDTO;
    }
}
