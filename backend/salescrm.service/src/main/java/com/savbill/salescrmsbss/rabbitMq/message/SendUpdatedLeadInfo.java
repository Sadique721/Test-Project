package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendUpdatedLeadInfo {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadFlowApproverUpdatedData;

//    public SendUpdatedLeadInfo(LeadMgmtWfDTO leadMgmtWfDTO) {
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.message = "Getting suitable staff for lead management approval";
//        leadFlowApproverUpdatedData.put("staffid",leadMgmtWfDTO.getStaffId().longValue());
//        leadFlowApproverUpdatedData.put("buid",leadMgmtWfDTO.getBuId());
//        leadFlowApproverUpdatedData.put("mvnoid",leadMgmtWfDTO.getMvnoId());
//        leadFlowApproverUpdatedData.put("status",leadMgmtWfDTO.getStatus());
//        leadFlowApproverUpdatedData.put("serviceareaid",leadMgmtWfDTO.getServiceareaid());
//        leadFlowApproverUpdatedData.put("nextapprover",leadMgmtWfDTO.getNextLeadApprover());
//    }
}