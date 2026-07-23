package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.CustMilestoneDetailsPojo;
import lombok.Data;

@Data
public class CustMilestoneDetailsMessage {

    private Long id;
    private String milestoneName;

    private Double amount;

    private String dueDate;
    private Long customerId;
    private Long leadId;

    public CustMilestoneDetailsMessage(CustMilestoneDetailsPojo pojo){
        if(pojo.getId() != null)
            this.id = pojo.getId();
        this.amount = pojo.getAmount();
        if(pojo.getCustomerId()!= null)
            this.customerId = pojo.getCustomerId();
        if(pojo.getLeadId() != null)
            this.leadId = pojo.getLeadId();
        this.dueDate = String.valueOf(pojo.getDueDate());
        this.milestoneName = pojo.getMilestoneName();
    }
}
