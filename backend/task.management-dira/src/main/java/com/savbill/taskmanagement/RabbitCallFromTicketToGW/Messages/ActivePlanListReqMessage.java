package com.savbill.taskmanagement.RabbitCallFromTicketToGW.Messages;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivePlanListReqMessage {
    private Integer customerId;

    public ActivePlanListReqMessage(Integer customerId){

        this.customerId =customerId;
    }
}
